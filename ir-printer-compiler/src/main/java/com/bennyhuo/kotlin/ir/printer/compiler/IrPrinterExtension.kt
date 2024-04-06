package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import com.bennyhuo.kotlin.ir.printer.compiler.output.KotlinLikeDumpOptions
import com.bennyhuo.kotlin.ir.printer.compiler.output.dumpKotlinLike
import com.bennyhuo.kotlin.ir.printer.compiler.output.dumpSrc
import java.io.File
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.util.dump

/**
 * Created by benny.
 */
const val OUTPUT_TYPE_RAW_IR = 0
const val OUTPUT_TYPE_KOTLIN_LIKE = 1
const val OUTPUT_TYPE_KOTLIN_LIKE_JC = 2

const val DEFAULT_INDENT = "  "

internal class IrSourcePrinterExtension : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val outputDirPath = Options.outputDirPath()
        logger.warn("/// generate, $outputDirPath")
        if (outputDirPath.isBlank()) return

        val outputDir = File(outputDirPath)

        moduleFragment.files.forEach { irFile ->
            outputDir.resolve(irFile.packageFqName.asString().replace('.', File.separatorChar)).run {
                logger.warn("/// + ${this.path}")
                mkdirs()
                val source = when (Options.outputType()) {
                    OUTPUT_TYPE_KOTLIN_LIKE_JC -> irFile.dumpSrc(DEFAULT_INDENT)
                    OUTPUT_TYPE_KOTLIN_LIKE -> irFile.dumpKotlinLike(
                        KotlinLikeDumpOptions(
                            printFileName = false,
                            printFilePath = false,
                            indent = DEFAULT_INDENT
                        )
                    )

                    else -> irFile.dump()
                }

                resolve("${irFile.name}.kt").writeText(source)
            }
        }
    }
}
