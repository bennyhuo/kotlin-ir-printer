package com.bennyhuo.kotlin.ir.printer.compiler

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

class Logger(private val messageCollector: MessageCollector) {

    fun info(message: Any?) {
        println(CompilerMessageSeverity.INFO, message)
    }

    fun warn(message: Any?) {
        println(CompilerMessageSeverity.WARNING, message)
    }

    fun error(message: Any?) {
        println(CompilerMessageSeverity.ERROR, message)
    }

    fun println(level: CompilerMessageSeverity, message: Any?) {
        messageCollector.report(level, "[Printer] ${message.toString()}")
    }

}
