package com.bennyhuo.kotlin.ir.printer.gradle

import com.bennyhuo.kotlin.ir.printer.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.konan.file.File

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
        val extension = project.extensions.getByType(PrinterExtension::class.java)
        val options = ArrayList<SubpluginOption>()
        options += SubpluginOption("outputType", extension.outputType.ordinal.toString())

        val outputDir = extension.outputDir ?: project.layout.buildDirectory.file("kotlin${File.separator}ir").get().asFile.path
        project.logger.warn("/// gradle plugin: $outputDir")
        options += SubpluginOption(
            "outputDir",
            outputDir
        )
        return project.provider { options }
    }
}
