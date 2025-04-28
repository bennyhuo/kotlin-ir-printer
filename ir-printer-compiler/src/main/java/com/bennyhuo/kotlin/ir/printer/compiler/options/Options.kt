package com.bennyhuo.kotlin.ir.printer.compiler.options

object Options {

    @JvmField
    val isOptimizedIrEnabled = Option(
        "isOptimizedIrEnabled",
        false,
        "Whether to print IR after IR lowering.",
        "",
    )

    @JvmField
    val indent = Option(
        "indent",
        "  ",
        "Indent of the printed source code. Default for two spaces.",
        "",
    )

    @JvmField
    val outputType = Option(
        "outputType",
        2,
        "Output type of the printed source code.",
        "0 for raw ir, 1 for builtin source code printer, 2 for Jetpack Compose style source code printer.",
    )

    @JvmField
    val outputDirPath = Option(
        "outputDir",
        "",
        "The root dir of the IR files.",
        "",
    )

    @JvmField
    val outputDirOptPath = Option(
        "outputDirOpt",
        "",
        "The root dir of the optimized IR files.",
        "",
    )


    val all = Options::class.java.declaredFields.filter {
        it.type == Option::class.java
    }.map {
        it.get(null) as Option<*>
    }
}
