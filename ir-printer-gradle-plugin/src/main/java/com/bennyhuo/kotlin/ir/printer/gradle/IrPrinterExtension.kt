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

    var isEnabled = true
    var indent = "  "
    var outputDir: String? = null
    var outputType: OutputType = OutputType.KOTLIN_LIKE_JETPACK_COMPOSE_STYLE

}
