package com.bennyhuo.kotlin.ir.printer.gradle

enum class OutputType {
    /**
     * Print IR declarations directly.
     * see [org.jetbrains.kotlin.ir.util.dump]
     */
    RAW_IR,

    /**
     * Use Kotlin builtin source printer.
     * see [org.jetbrains.kotlin.ir.util.dumpKotlinLike]
     */
    KOTLIN_LIKE,

    /**
     * Use the source printer from Jetpack Compose.
     * It looks better than the Kotlin builtin printer.
     */
    KOTLIN_LIKE_JETPACK_COMPOSE_STYLE
}

open class PrinterExtension {

    var isEnabled: Boolean = true
    var isOptimizedKotlinIrEnabled: Boolean = false
    var isLlvmIrEnabled: Boolean = true
    var llvmDisPath: String? = null
    var indent: String = "  "
    var outputDir: String? = null
    var outputType: OutputType = OutputType.RAW_IR

}
