package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import java.util.Properties
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.utils.NativeCompilerDownloader
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.util.DependencyDirectories

val Project.konanDataDir: String?
    get() = findProperty("konan.data.dir")?.toString()

val Project.nativeHome: String?
    get() = findProperty("kotlin.native.home")?.toString()

class KonanConfig(val project: Project) {
    val konanHome: File = (project.konanDataDir?.let { NativeCompilerDownloader(project).compilerDirectory }
        ?: project.nativeHome?.let { project.file(it) }
        ?: NativeCompilerDownloader(project).compilerDirectory).absoluteFile

    val konanProperties = Properties().apply {
        project.file("${konanHome}/konan/konan.properties").inputStream().use(::load)
    }

    val dependenciesRoot: File = DependencyDirectories.getDependenciesRoot(project.konanDataDir)

    val llvmHome: File = File(
        dependenciesRoot,
        konanProperties.getProperty("llvm.${HostManager.host.name}.dev")
            ?: throw Exception("Cannot find llvm home for current target: ${HostManager.host}")
    )
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
    println("Konan temporary dir is set to: $userDefinedKonanDir by use.")
    return userDefinedKonanDir
}
