package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Option
import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

lateinit var logger: Logger

@ExperimentalCompilerApi
@AutoService(CompilerPluginRegistrar::class)
class IrPrinterCompilerPluginRegistrar : CompilerPluginRegistrar() {

    private val extension = IrSourcePrinterExtension()

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        logger = Logger(configuration.get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY))

        Option.initialize(configuration)
        IrGenerationExtension.registerExtension(extension)
        if (Options.isOptimizedIrEnabled()) {
            registerPrinterForOptimizedIr()
        }
    }
}
