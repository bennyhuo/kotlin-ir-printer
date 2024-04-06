package com.bennyhuo.kotlin.compiletesting.extensions.ir

import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtil
import java.util.LinkedList

/**
 * Created by benny.
 */
internal fun String.escapeCharacters(): String {
    return StringUtil.escapeCharCharacters(this)
}

/**
 * It is not easy to determine which is better.
 */
internal fun String.rawStringPreferred(): Boolean? {
    var newLineCount = 0
    val countStack = LinkedList<Int>()
    var currentCount = 0
    val specialChars = arrayOf('\t', '\b', '\r', '\$')
    for (char in this) {
        if (char in specialChars) {
            return false
        }

        if (char == '\n') {
            newLineCount++
            countStack.push(currentCount)
            currentCount = 0
        } else if (!char.isWhitespace()) {
            currentCount++
        }
    }
    countStack.push(currentCount)
    if (newLineCount > 1 && countStack.filter { it > 0 }.size > 2) {
        return true
    }
    return null
}