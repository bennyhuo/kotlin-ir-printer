package com.bennyhuo.kotlin.ir.printer.compiler.options

object Options {

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
        "The root dir of the output source files.",
        "",
    )

    val all = Options::class.java.declaredFields.filter {
        it.type == Option::class.java
    }.map {
        it.get(null) as Option<*>
    }
}
