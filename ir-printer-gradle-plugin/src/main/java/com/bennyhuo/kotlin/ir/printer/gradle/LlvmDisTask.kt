package com.bennyhuo.kotlin.ir.printer.gradle

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
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
    abstract val llvmDisPath: Property<Lazy<String>>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val konanTempDir: Property<String>

    @get:OutputDirectory
    abstract val outputPath: Property<String>

    @TaskAction
    fun run() {
        val inputDir = File(konanTempDir.get())
        val llvmDisPathValue = llvmDisPath.get().value
        inputDir.list()?.filter { it.endsWith(".bc") }?.forEach {
            val result = executeCommand("$llvmDisPathValue $it", inputDir, logger)
            if(!result.isOk) {
                throw GradleException("Failed to disassemble $it.")
            }
        }
        inputDir.listFiles()?.filter { it.name.endsWith(".ll") }?.forEach {
            it.renameTo(File(outputPath.get(), it.name))
        }
        // copy api.cpp if exists.
        inputDir.listFiles()?.filter { it.name.endsWith(".cpp") }?.forEach {
            it.copyTo(File(outputPath.get(), it.name), overwrite = true)
        }
    }
}