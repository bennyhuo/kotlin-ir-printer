package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * Created by benny.
 */
internal class IrSourcePrinterExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        printIr(moduleFragment, Options.outputDirPath())
    }
}
