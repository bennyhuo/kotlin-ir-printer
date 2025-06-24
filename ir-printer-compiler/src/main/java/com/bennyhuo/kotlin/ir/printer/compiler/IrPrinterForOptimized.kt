@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import org.jetbrains.kotlin.backend.common.CommonBackendContext
import org.jetbrains.kotlin.backend.common.phaser.Action
import org.jetbrains.kotlin.backend.common.phaser.ActionState
import org.jetbrains.kotlin.backend.common.phaser.BeforeOrAfter
import org.jetbrains.kotlin.backend.common.phaser.NamedCompilerPhase
import org.jetbrains.kotlin.backend.common.phaser.SimpleNamedCompilerPhase
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.jvmLoweringPhases
import org.jetbrains.kotlin.backend.konan.NativeGenerationState
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenInput
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenPhase
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * Created by benny.
 */
fun registerPrinterForOptimizedIr() {
    registerPrinterForJvmTargets()
    registerPrinterForNativeTargets()
    registerPrinterForWasmTargets()
}

private fun getIrPrinterForJvmTargets(outputDirOptPath: String): Action<IrModuleFragment, JvmBackendContext> =
    fun(state: ActionState, data: IrModuleFragment, _: JvmBackendContext) {
        if (state.beforeOrAfter == BeforeOrAfter.AFTER) {
            printIr(data, outputDirOptPath)
        }
    }

private fun registerPrinterForJvmTargets() {
    try {
        val actionsField = NamedCompilerPhase::class.java.getDeclaredField("actions")
        actionsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val actions = actionsField.get(jvmLoweringPhases) as MutableSet<Action<IrModuleFragment, JvmBackendContext>>
        actionsField.set(jvmLoweringPhases, actions + getIrPrinterForJvmTargets(Options.outputDirOptPath()))

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
        val preactionsField = SimpleNamedCompilerPhase::class.java.getDeclaredField("preactions")
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

private fun getIrPrinterForWasmTargets(outputDirOptPath: String): Action<IrModuleFragment, CommonBackendContext> =
    fun(_: ActionState, data: IrModuleFragment, _: CommonBackendContext) {
        printIr(data, outputDirOptPath)
    }

private fun registerPrinterForWasmTargets() {
    try {
        val preactionsField = SimpleNamedCompilerPhase::class.java.getDeclaredField("preactions")
        preactionsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val wasmLoweringPhasesKtClass = Class.forName("org.jetbrains.kotlin.backend.wasm.WasmLoweringPhasesKt")
        val validateIrAfterLoweringField = wasmLoweringPhasesKtClass.getDeclaredField("validateIrAfterLowering")
        validateIrAfterLoweringField.isAccessible = true
        val preactions = preactionsField.get(validateIrAfterLoweringField.get(null)) as MutableSet<Action<IrModuleFragment, CommonBackendContext>>
        preactions.add(getIrPrinterForWasmTargets(Options.outputDirOptPath()))

        logger.warn("Register the printer of optimized IR for wasm targets.")
    } catch (_: NoClassDefFoundError) {
        logger.info("The printer of optimized IR for wasm targets is ignored.")
    } catch (t: Throwable) {
        logger.warn("Failed to register the printer of optimized IR for wasm targets: $t")
    }
}