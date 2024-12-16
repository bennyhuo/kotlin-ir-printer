package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import java.util.Properties
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.utils.NativeCompilerDownloader
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.ArchiveType
import org.jetbrains.kotlin.konan.util.DependencyDirectories
import org.jetbrains.kotlin.konan.util.DependencyProcessor

val Project.konanDataDir: String?
    get() = findProperty("konan.data.dir")?.toString()

val Project.nativeHome: String?
    get() = findProperty("kotlin.native.home")?.toString()

class KonanConfig(project: Project) {
    val konanDataDir = project.konanDataDir
    
    val konanHome: File = (konanDataDir?.let { NativeCompilerDownloader(project).compilerDirectory }
        ?: project.nativeHome?.let { project.file(it) }
        ?: NativeCompilerDownloader(project).compilerDirectory).absoluteFile

    val konanProperties by lazy {
        Properties().apply {
            File("${konanHome}/konan/konan.properties").inputStream().use(::load)
        }
    }

    val dependenciesRoot = DependencyDirectories.getDependenciesRoot(konanDataDir)

    val llvmHome: File by lazy {
        val llvmHomeName = konanProperties.getProperty("llvm.${HostManager.host.name}.dev")
            ?: throw Exception("Cannot find llvm home for current target: ${HostManager.host}")
        val file = File(dependenciesRoot, llvmHomeName)
        
        if (!file.exists()) {
            DependencyProcessor(
                dependenciesRoot = dependenciesRoot,
                properties = konanProperties,
                dependencies = listOf(llvmHomeName),
                archiveType = defaultArchiveTypeByHost(HostManager.host),
                customProgressCallback = { url, currentBytes, totalBytes ->
                    print("\nDownloading dependency: $url (${currentBytes.humanReadable}/${totalBytes.humanReadable}). ")
                }
            ).run()
        }
        
        file
    }

    private val Long.humanReadable: String
        get() {
            if (this < 0) {
                return "-"
            }
            if (this < 1024) {
                return "$this bytes"
            }
            val exp = (Math.log(this.toDouble()) / Math.log(1024.0)).toInt()
            val prefix = "kMGTPE"[exp-1]
            return "%.1f %siB".format(this / Math.pow(1024.0, exp.toDouble()), prefix)
        }

    private fun defaultArchiveTypeByHost(host: KonanTarget): ArchiveType = when (host) {
        KonanTarget.LINUX_X64, KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> ArchiveType.TAR_GZ
        KonanTarget.MINGW_X64 -> ArchiveType.ZIP
        else -> error("$host can't be a host platform!")
    }
}

fun KotlinCompilation<*>.getOrConfigKonanTempDir(): String {
    val optionKey = "-Xtemporary-files-dir"
    val userDefinedKonanDir = compileTaskProvider.get().compilerOptions.freeCompilerArgs.orNull?.find {
        it.startsWith("$optionKey=")
    }?.split("=", limit = 2)?.last()
    if (userDefinedKonanDir == null) {
        val defaultKonanDir = project.output(target, "llvm-bc")
        compileTaskProvider.get().compilerOptions.freeCompilerArgs.add("$optionKey=$defaultKonanDir")
        println("Konan temporary dir is set to: $defaultKonanDir by default.")
        return defaultKonanDir
    }
    println("Konan temporary dir is set to: $userDefinedKonanDir by user.")
    return userDefinedKonanDir
}
