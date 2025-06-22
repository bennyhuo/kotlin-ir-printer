package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary

/**
 * Created by benny.
 */
fun Project.output(extension: PrinterExtension, binary: NativeBinary, path: String): String {
    return output(extension, path, binary.buildType.getName(), binary.target.targetName)
}

fun Project.output(extension: PrinterExtension, target: KotlinTarget, path: String): String {
    return output(extension, path, target.targetName)
}

fun Project.output(extension: PrinterExtension, vararg paths: String): String {
    val userConfiguredOutputDir = extension.outputDir
    return if (userConfiguredOutputDir.isNullOrBlank()) {
        layout.buildDirectory.file(
            listOf("outputs", "kotlin", *paths).joinToString(File.separator)
        ).get().asFile.path
    } else {
        File(userConfiguredOutputDir, paths.joinToString(File.separator)).path
    }
}