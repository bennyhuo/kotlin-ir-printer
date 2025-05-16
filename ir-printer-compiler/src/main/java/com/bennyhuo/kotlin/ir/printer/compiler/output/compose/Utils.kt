package com.bennyhuo.kotlin.ir.printer.compiler.output.compose

import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isLambda
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.exceptions.rethrowIntellijPlatformExceptionIfNeeded

val IrFunction.namedParameters
    get() = parameters.filter { it.kind == IrParameterKind.Regular || it.kind == IrParameterKind.Context }

fun IrAnnotationContainer.hasComposableAnnotation(): Boolean =
    hasAnnotation(ClassId(FqName("androidx.compose.runtime"), Name.identifier("Composable")))

fun IrFunction.firstParameterOfKind(kind: IrParameterKind) =
    parameters.firstOrNull { it.kind == kind }

inline fun <T> includeFileNameInExceptionTrace(file: IrFile, body: () -> T): T {
    try {
        return body()
    } catch (e: Exception) {
        rethrowIntellijPlatformExceptionIfNeeded(e)
        throw Exception("IR lowering failed at: ${file.name}", e)
    }
}

val IrValueParameter.isReceiver
    get() = kind == IrParameterKind.ExtensionReceiver || kind == IrParameterKind.DispatchReceiver

fun IrExpression.unwrapLambda(): IrFunctionSymbol? = when {
    this is IrBlock && origin.isLambdaBlockOrigin ->
        (statements.lastOrNull() as? IrFunctionReference)?.symbol

    this is IrFunctionExpression ->
        function.symbol

    else ->
        null
}

private val IrStatementOrigin?.isLambdaBlockOrigin: Boolean
    get() = isLambda || this == IrStatementOrigin.ADAPTED_FUNCTION_REFERENCE ||
            this == IrStatementOrigin.SUSPEND_CONVERSION
