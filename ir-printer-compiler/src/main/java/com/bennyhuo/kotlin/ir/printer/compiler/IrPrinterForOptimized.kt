@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import org.jetbrains.kotlin.backend.common.phaser.Action
import org.jetbrains.kotlin.backend.common.phaser.ActionState
import org.jetbrains.kotlin.backend.common.phaser.SimpleNamedCompilerPhase
import org.jetbrains.kotlin.backend.konan.NativeGenerationState
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenInput
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenPhase

/**
 * Created by benny.
 */
private fun getIrPrinterBeforeCodegen(outputDirOptPath: String): Action<CodegenInput, NativeGenerationState> =
    fun(_: ActionState, data: CodegenInput, _: NativeGenerationState) {
        logger.warn("Print optimized IR to ${outputDirOptPath}, module: ${data.irModule.name}.")
        printIr(data.irModule, outputDirOptPath)
    }

@Suppress("UNCHECKED_CAST")
fun registerPrinterForOptimizedIr() {
    try {
        val preactionsField = SimpleNamedCompilerPhase::class.java.getDeclaredField("preactions")
        preactionsField.isAccessible = true
        val preactions =
            preactionsField.get(CodegenPhase) as MutableSet<Action<CodegenInput, NativeGenerationState>>
        preactions.add(getIrPrinterBeforeCodegen(Options.outputDirOptPath()))

        logger.warn("Optimized IR is setup.")
    } catch (e: Exception) {
        logger.warn("Optimized IR is not supported: $e")
        e.printStackTrace()
    }
}