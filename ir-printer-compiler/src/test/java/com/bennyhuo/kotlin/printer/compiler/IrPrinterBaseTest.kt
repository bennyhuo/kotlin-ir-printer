package com.bennyhuo.kotlin.printer.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.checkResult
import com.bennyhuo.kotlin.compiletesting.extensions.source.FileBasedModuleInfoLoader
import com.bennyhuo.kotlin.ir.printer.compiler.IrPrinterCompilerPluginRegistrar
import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

/**
 * Created by benny at 2022/1/15 8:55 PM.
 */
@OptIn(ExperimentalCompilerApi::class)
abstract class IrPrinterBaseTest {

    abstract val outputType: Int
    abstract val subDirName: String

    protected fun testBase(fileName: String) {
        Options.outputType.set(outputType)

        val loader = FileBasedModuleInfoLoader("testData/$subDirName/$fileName")
        val moduleInfo = loader.loadSourceModuleInfos().singleOrNull() ?: throw IllegalStateException("Only single module is supported by now.")
        val expectModuleInfo = loader.loadExpectModuleInfos().singleOrNull() ?: throw IllegalStateException("Only single module is supported by now.")

        val module = KotlinModule(moduleInfo, true, compilerPluginRegistrars = listOf(IrPrinterCompilerPluginRegistrar()))

        val outputFile = module.workingDir.resolve("irPrinter")
        module.customizedOutputDirs += outputFile
        Options.outputDirPath.set(outputFile.path)

        module.checkResult(expectModuleInfo)
    }

}
