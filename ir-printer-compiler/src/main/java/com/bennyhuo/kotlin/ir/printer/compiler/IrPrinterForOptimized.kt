@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.jvmLoweringPhases
import org.jetbrains.kotlin.backend.konan.NativeGenerationState
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenInput
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenPhase
import org.jetbrains.kotlin.config.phaser.Action
import org.jetbrains.kotlin.config.phaser.ActionState
import org.jetbrains.kotlin.config.phaser.BeforeOrAfter
import org.jetbrains.kotlin.config.phaser.NamedCompilerPhase
import org.jetbrains.kotlin.config.phaser.PhaseConfig
import org.jetbrains.kotlin.config.phaser.PhaserState
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * Created by benny.
 */
fun registerPrinterForOptimizedIr() {
    registerPrinterForJvmTargets()
    registerPrinterForNativeTargets()
}

private class JvmIrPrinterOpt(private val outputPath: String): NamedCompilerPhase<JvmBackendContext, IrModuleFragment, IrModuleFragment>("JvmIrPrinterOpt"){
    override fun phaseBody(
        context: JvmBackendContext,
        input: IrModuleFragment
    ): IrModuleFragment {
        printIr(input, outputPath)
        return input
    }

    override fun outputIfNotEnabled(phaseConfig: PhaseConfig, phaserState: PhaserState, context: JvmBackendContext, input: IrModuleFragment) = input
}

private fun registerPrinterForJvmTargets() {
    try {
        (jvmLoweringPhases as MutableList<NamedCompilerPhase<JvmBackendContext, IrModuleFragment, IrModuleFragment>>).add(JvmIrPrinterOpt(Options.outputDirOptPath()))
        logger.warn("Register the printer of optimized IR for JVM targets.")
    } catch (_: NoClassDefFoundError) {
        logger.info("The printer of optimized IR for JVM targets is ignored.")
    } catch (t: Throwable) {
        logger.warn("Failed to register the printer of optimized IR for JVM targets: $t")
    }
}

private fun getIrPrinterForNativeTargets(outputDirOptPath: String): Action<CodegenInput, NativeGenerationState> =
    fun(_: ActionState, data: CodegenInput, _: NativeGenerationState) {
        printIr(data.irModule, outputDirOptPath)
    }

private fun registerPrinterForNativeTargets() {
    try {
        val preactionsField = NamedCompilerPhase::class.java.getDeclaredField("preactions")
        preactionsField.isAccessible = true
        @Suppress("UNCHECKED_CAST") 
        val preactions = preactionsField.get(CodegenPhase) as MutableSet<Action<CodegenInput, NativeGenerationState>>
        preactions.add(getIrPrinterForNativeTargets(Options.outputDirOptPath()))

        logger.warn("Register the printer of optimized IR for native targets.")
    } catch (_: NoClassDefFoundError) {
        logger.info("The printer of optimized IR for native targets is ignored.")
    } catch (t: Throwable) {
        logger.warn("Failed to register the printer of optimized IR for native targets: $t")
    }
}