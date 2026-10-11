@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.bennyhuo.kotlin.ir.printer.compiler

import com.bennyhuo.kotlin.ir.printer.compiler.options.Options
import java.util.concurrent.CopyOnWriteArraySet
import org.jetbrains.kotlin.backend.common.LoweringContext
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.jvmLoweringPhases
import org.jetbrains.kotlin.backend.konan.NativeGenerationState
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenInput
import org.jetbrains.kotlin.backend.konan.driver.phases.CodegenPhase
import org.jetbrains.kotlin.backend.wasm.wasmLowerings
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.phaser.Action
import org.jetbrains.kotlin.config.phaser.ActionState
import org.jetbrains.kotlin.config.phaser.NamedCompilerPhase
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * Created by benny.
 */
fun registerPrinterForOptimizedIr(configuration: CompilerConfiguration) {
    registerPrinterForJvmTargets()
    registerPrinterForNativeTargets(configuration)
    registerPrinterForWasmTargets()
}

private fun <Context, Data> NamedCompilerPhase<*, *, *>.addPreaction(
    action: Action<Data, Context>,
    replaceByClassName: Boolean = false,
) {
    synchronized(NamedCompilerPhase::class.java) {
        val preactionsField = NamedCompilerPhase::class.java.getDeclaredField("preactions")
        preactionsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val current = preactionsField.get(this) as Set<Action<Data, Context>>
        val copyOnWriteSet = current as? CopyOnWriteArraySet<Action<Data, Context>>
            ?: CopyOnWriteArraySet(current).also {
                preactionsField.set(this, it)
            }
        if (replaceByClassName) {
            copyOnWriteSet.removeIf { it.javaClass.name == action.javaClass.name }
        }
        copyOnWriteSet.add(action)
    }
}

private object JvmIrPrinterOpt : Action<IrModuleFragment, JvmBackendContext> {
    override fun invoke(p1: ActionState, data: IrModuleFragment, context: JvmBackendContext) {
        val configuration = context.configuration
        if (Options.isOptimizedIrEnabled[configuration]) {
            printIr(data, Options.outputDirOptPath[configuration], configuration)
        }
    }
}

private fun registerPrinterForJvmTargets() {
    try {
        jvmLoweringPhases.last().addPreaction(JvmIrPrinterOpt)
        logger.warn("Register the printer of optimized IR for JVM targets.")
    } catch (_: NoClassDefFoundError) {
        logger.info("The printer of optimized IR for JVM targets is ignored.")
    } catch (t: Throwable) {
        logger.warn("Failed to register the printer of optimized IR for JVM targets: $t")
    }
}

private class NativeIrPrinterOpt(
    private val configuration: CompilerConfiguration,
) : Action<CodegenInput, NativeGenerationState> {
    override fun invoke(p1: ActionState, data: CodegenInput, state: NativeGenerationState) {
        if (Options.isOptimizedIrEnabled[configuration]) {
            printIr(data.irModule, Options.outputDirOptPath[configuration], configuration)
        }
    }
}

private fun registerPrinterForNativeTargets(configuration: CompilerConfiguration) {
    try {
        CodegenPhase.addPreaction(NativeIrPrinterOpt(configuration), replaceByClassName = true)
        logger.warn("Register the printer of optimized IR for native targets.")
    } catch (_: NoClassDefFoundError) {
        logger.info("The printer of optimized IR for native targets is ignored.")
    } catch (t: Throwable) {
        logger.warn("Failed to register the printer of optimized IR for native targets: $t")
    }
}

private object WasmIrPrinterOpt : Action<IrModuleFragment, LoweringContext> {
    override fun invoke(p1: ActionState, data: IrModuleFragment, context: LoweringContext) {
        val configuration = context.configuration
        if (Options.isOptimizedIrEnabled[configuration]) {
            printIr(data, Options.outputDirOptPath[configuration], configuration)
        }
    }
}

private fun registerPrinterForWasmTargets() {
    try {
        wasmLowerings.last().addPreaction(WasmIrPrinterOpt)
        logger.warn("Register the printer of optimized IR for wasm targets.")
    } catch (_: NoClassDefFoundError) {
        logger.info("The printer of optimized IR for wasm targets is ignored.")
    } catch (t: Throwable) {
        logger.warn("Failed to register the printer of optimized IR for wasm targets: $t")
    }
}