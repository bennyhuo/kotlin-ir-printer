package com.bennyhuo.kotlin.ir.printer.gradle

import com.bennyhuo.kotlin.ir.printer.BuildConfig
import java.io.File
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class IrPrinterGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create("irPrinter", PrinterExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.project.extensions.getByType(PrinterExtension::class.java).isEnabled
    }

    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
        artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
        version = BuildConfig.KOTLIN_PLUGIN_VERSION
    )

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val target = kotlinCompilation.target
        val extension = project.extensions.getByType(PrinterExtension::class.java)

        if (
            extension.isLlvmIrEnabled &&
            kotlinCompilation.name == KotlinCompilation.MAIN_COMPILATION_NAME &&
            target is KotlinNativeTarget
        ) {
            val konanTempDir = kotlinCompilation.getOrConfigKonanTempDir(extension)

            val userConfiguredLlvmDisPath = extension.llvmDisPath
            val llvmDisPathLazy = if (userConfiguredLlvmDisPath.isNullOrBlank()) {
                val konanConfig = KonanConfig(project)

                // force llvm variant to dev to obtain the llvm-dis binary file.
                kotlinCompilation.compileTaskProvider.get().compilerOptions.freeCompilerArgs.add("-Xllvm-variant=dev")
                println("LLVM variant is set to dev for disassembling LLVM bitcode.")

                // lazily resolve this path after Kotlin Native compiler setup.
                lazy {
                    File(konanConfig.llvmHome, "bin/llvm-dis").absolutePath
                }
            } else {
                println("Use user configured llvm-dis in '$userConfiguredLlvmDisPath' for disassembling LLVM bitcode.")
                lazy { File(userConfiguredLlvmDisPath).absolutePath }
            }

            target.binaries.forEach { binary ->
                val taskName = "disassemble${binary.name.capitalized()}${target.targetName.capitalized()}Bitcode"
                project.tasks.register(taskName, LlvmDisTask::class.java) {
                    it.dependsOn(binary.linkTaskName)
                    it.llvmDisPath.set(llvmDisPathLazy)
                    it.konanTempDir.set(konanTempDir)
                    it.outputPath.set(project.output(extension, target, "llvm-ir"))
                }
            }
        }

        val options = ArrayList<SubpluginOption>()
        options += SubpluginOption("isOptimizedIrEnabled", extension.isOptimizedKotlinIrEnabled.toString())
        options += SubpluginOption("indent", extension.indent)
        options += SubpluginOption("outputType", extension.outputType.ordinal.toString())
        options += SubpluginOption(
            "outputDir",
            project.output(extension, target, "ir")
        )
        options += SubpluginOption(
            "outputDirOpt",
            project.output(extension, target, "ir-opt")
        )
        return project.provider { options }
    }
}
