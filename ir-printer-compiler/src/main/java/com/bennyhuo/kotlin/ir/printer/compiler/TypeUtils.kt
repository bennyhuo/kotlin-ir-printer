package com.bennyhuo.kotlin.ir.printer.compiler

/**
 * Created by benny.
 */
inline fun <reified T : Any> Any?.safeAs(): T? = this as? T
