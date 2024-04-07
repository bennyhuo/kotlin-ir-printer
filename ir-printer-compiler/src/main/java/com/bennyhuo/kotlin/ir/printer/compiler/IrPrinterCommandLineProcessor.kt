package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.BuildConfig
import com.bennyhuo.kotlin.ir.printer.compiler.options.Option
import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class IrPrinterCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = Options.all.map { it.option }

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        super.processOption(option, value, configuration)
        Option.process(option, value, configuration)
    }

}
