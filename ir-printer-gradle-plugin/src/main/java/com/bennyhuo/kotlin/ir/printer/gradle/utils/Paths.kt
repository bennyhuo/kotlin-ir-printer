package com.bennyhuo.kotlin.ir.printer.gradle.utils

import com.bennyhuo.kotlin.ir.printer.gradle.PrinterExtension
import java.io.File
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary

/**
 * Created by benny.
 */
fun Project.output(extension: PrinterExtension, binary: JsIrBinary, path: String): String {
    return output(extension, path, binary.mode.name.lowercase(), binary.target.targetName)
}

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