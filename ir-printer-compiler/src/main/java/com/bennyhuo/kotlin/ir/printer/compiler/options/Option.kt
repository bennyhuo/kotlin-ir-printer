package com.bennyhuo.kotlin.ir.printer.compiler.options

import com.bennyhuo.kotlin.ir.printer.compiler.logger
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.lang.ref.WeakReference

class Option<T : Any?>(
    name: String,
    private val defaultValue: T,
    description: String,
    valueDescription: String,
    required: Boolean = false,
    allowMultipleOccurrences: Boolean = false,
) {

    private val key = CompilerConfigurationKey.create<T>(name)
    private var value: T? = null

    val option = CliOption(
        name,
        valueDescription,
        description,
        required,
        allowMultipleOccurrences,
    )

    fun config(value: String?, configuration: CompilerConfiguration) {
        val typedValue = when (defaultValue) {
            is Boolean -> value?.toBooleanStrictOrNull() ?: defaultValue
            is Int -> value?.toIntOrNull() ?: defaultValue
            else -> value
        } as T

        if (typedValue != null) {
            configuration.put(key, typedValue)
        }
    }

    operator fun invoke(): T {
        return value ?: (compilerConfiguration?.get()?.get(key) ?: defaultValue).also {
            value = it
        }
    }

    fun set(value: T) {
        this.value = value
    }

    companion object {
        private var compilerConfiguration: WeakReference<CompilerConfiguration>? = null

        fun process(
            option: AbstractCliOption,
            value: String,
            configuration: CompilerConfiguration,
        ) {
            Options.all.firstOrNull {
                it.option == option
            }?.config(value, configuration)
        }

        fun initialize(compilerConfiguration: CompilerConfiguration) {
            Companion.compilerConfiguration = WeakReference(compilerConfiguration)
        }

        fun dump() {
            logger.warn(
                Options.all.joinToString {
                    "${it.option.optionName}: ${it()}"
                },
            )
        }
    }
}
