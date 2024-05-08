package com.bennyhuo.kotlin.printer.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.IR_OUTPUT_TYPE_KOTLIN_LIKE_JC
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

/**
 * Created by benny at 2022/1/15 8:55 PM.
 */
@OptIn(ExperimentalCompilerApi::class)
class IrPrinterJcTest : IrPrinterBaseTest() {

    @Test
    fun enum() {
        testBase("enums.kt")
    }

    override val outputType: Int = IR_OUTPUT_TYPE_KOTLIN_LIKE_JC
    override val subDirName: String = "jc"
}
