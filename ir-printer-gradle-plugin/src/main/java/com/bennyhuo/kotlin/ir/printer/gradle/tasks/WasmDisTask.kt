package com.bennyhuo.kotlin.ir.printer.gradle.tasks

import com.bennyhuo.kotlin.ir.printer.gradle.utils.executeCommand
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
abstract class WasmDisTask : DefaultTask() {

    init {
        group = IR_PRINTER_TASK_GROUP
    }

    @get:Input
    abstract val wasmDisPath: Property<Lazy<String>>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputPath: Property<String>

    @get:OutputDirectory
    abstract val outputPath: Property<String>

    @TaskAction
    fun run() {
        val inputDir = File(inputPath.get())
        val llvmDisPathValue = wasmDisPath.get().value
        inputDir.list()?.filter { it.endsWith(".wasm") }?.forEach {
            val sourceMapFile = File(inputDir, "$it.map")
            val sourceMapArgs = if (sourceMapFile.exists()) "-sm ${sourceMapFile.name}" else ""
            val outputFileName = "${it.removeSuffix(".wasm")}.wat"
            val result = executeCommand("$llvmDisPathValue $it -o $outputFileName $sourceMapArgs", inputDir, logger)
            if(!result.isOk) {
                throw GradleException("Failed to disassemble $it.")
            }
        }
        inputDir.listFiles()?.filter { it.name.endsWith(".wat") }?.forEach {
            it.renameTo(File(outputPath.get(), it.name))
        }
    }
}