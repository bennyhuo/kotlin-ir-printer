package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by benny.
 */
@CacheableTask
abstract class LlvmDisTask : DefaultTask() {

    init {
        group = "build"
    }

    @get:Input
    abstract val llvmDisPath: Property<String>

    @get:Input
    abstract val konanTempDir: Property<String>

    @get:OutputDirectory
    abstract val outputPath: Property<String>

    @TaskAction
    fun run() {
        val inputDir = File(konanTempDir.get())
        inputDir.list()?.filter { it.endsWith(".bc") }?.forEach {
            val result = executeCommand("${llvmDisPath.get()} $it", inputDir, project.logger)
            if(!result.isOk) {
                throw GradleException("Failed to disassemble $it.")
            }
        }
        inputDir.listFiles()?.filter { it.name.endsWith(".ll") }?.forEach {
            it.renameTo(File(outputPath.get(), it.name))
        }
    }
}