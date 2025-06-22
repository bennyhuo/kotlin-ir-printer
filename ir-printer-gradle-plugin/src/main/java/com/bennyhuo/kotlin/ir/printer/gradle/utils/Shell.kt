package com.bennyhuo.kotlin.ir.printer.gradle.utils

import java.io.File
import java.util.concurrent.TimeUnit
import org.gradle.api.logging.Logger

const val EXIT_CODE_TIMEOUT = 1000
const val EXIT_CODE_ERROR = 1001

class CommandResult(
    val exitCode: Int,
    val stdout: List<String>,
) {
    private var hasErrorLog = false

    val isOk: Boolean
        get() = !hasErrorLog && exitCode == 0

    fun resolveOutput(logger: Logger? = null) {
        stdout.forEach {
            logger?.lifecycle(it)
        }
    }
}

fun executeCommand(cmd: String, dir: File, logger: Logger? = null): CommandResult {
    val result = try {
        val process = ProcessBuilder().directory(dir)
            .redirectErrorStream(true)
            .command(cmd.split(" "))
            .start()
        logger?.lifecycle("$ $cmd")

        val stdout = process.inputStream.bufferedReader().readLines()
        val exitCode = if (process.waitFor(30, TimeUnit.SECONDS)) {
            process.exitValue()
        } else {
            logger?.warn("Timeout to execute '$cmd' in $dir after 30 seconds.")
            EXIT_CODE_TIMEOUT
        }
        CommandResult(exitCode, stdout)
    } catch (e: Exception) {
        logger?.warn("Failed to execute '$cmd' in $dir: ${e.message}")
        CommandResult(EXIT_CODE_ERROR, emptyList())
    }
    result.resolveOutput(logger)
    return result
}
