package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

/**
 * Created by benny.
 */
fun Project.output(target: KotlinTarget, path: String): String {
    return layout.buildDirectory.file(
        listOf("outputs", "kotlin", path, target.targetName).joinToString(File.separator)
    ).get().asFile.path
}