package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import com.bennyhuo.kotlin.ir.printer.compiler.output.builtin.KotlinLikeDumpOptions
import com.bennyhuo.kotlin.ir.printer.compiler.output.builtin.dumpKotlinLike
import com.bennyhuo.kotlin.ir.printer.compiler.output.compose.dumpSrc
import java.io.File
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.util.dump

/**
 * Created by benny.
 */
const val OUTPUT_TYPE_RAW_IR = 0
const val OUTPUT_TYPE_KOTLIN_LIKE = 1
const val OUTPUT_TYPE_KOTLIN_LIKE_JC = 2

internal fun printIr(moduleFragment: IrModuleFragment, outputDirPath: String) {
    if (outputDirPath.isBlank()) return
    val moduleName = moduleFragment.name.asStringStripSpecialMarkers()
    val outputDir = File(outputDirPath, moduleName)
    outputDir.deleteRecursively()
    logger.warn("Print IR to ${outputDir.path}, module: $moduleName.")

    val indent = Options.indent().takeIf { it.isNotEmpty() } ?: "  "
    val outputType = Options.outputType()

    moduleFragment.files.forEach { irFile ->
        outputDir.resolve(irFile.packageFqName.asString().replace('.', File.separatorChar)).run {
            mkdirs()
            val source = try {
                when (outputType) {
                    OUTPUT_TYPE_KOTLIN_LIKE_JC -> irFile.dumpSrc(indent)
                    OUTPUT_TYPE_KOTLIN_LIKE -> irFile.dumpKotlinLike(
                        KotlinLikeDumpOptions(
                            printFileName = false,
                            printFilePath = false,
                            indent = indent
                        )
                    )

                    else -> irFile.dump()
                }
            } catch (e: Exception) {
                buildString {
                    appendLine("Failed to print IR of ${irFile.path} as type ${getOutputTypeName(outputType)}.")
                    appendLine()
                    append(e.stackTraceToString())
                }
            }

            resolve(irFile.name).writeText(source)
        }
    }
}

private fun getOutputTypeName(outputType: Int): String {
    return when(outputType) {
        OUTPUT_TYPE_KOTLIN_LIKE_JC -> "OUTPUT_TYPE_KOTLIN_LIKE_JC"
        OUTPUT_TYPE_KOTLIN_LIKE -> "OUTPUT_TYPE_KOTLIN_LIKE"
        else -> "OUTPUT_TYPE_RAW_IR"
    }
}