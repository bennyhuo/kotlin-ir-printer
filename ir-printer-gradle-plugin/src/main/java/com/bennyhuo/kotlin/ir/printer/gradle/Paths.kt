package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

/**
 * Created by benny.
 */
fun Project.output(extension: PrinterExtension, target: KotlinTarget, path: String): String {
    val userConfiguredOutputDir = extension.outputDir
    return if (userConfiguredOutputDir.isNullOrBlank()) {
        layout.buildDirectory.file(
            listOf("outputs", "kotlin", path, target.targetName).joinToString(File.separator)
        ).get().asFile.path
    } else {
        File(userConfiguredOutputDir, listOf(path, target.targetName).joinToString(File.separator)).path
    }
}