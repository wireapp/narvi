package com.wire.bots.narvi.utils

import java.util.UUID

fun String.toUuid(): UUID = UUID.fromString(this)

/**
 * Convert current statement to nothing.
 *
 * Useful when you need exhaustive `when` returning Unit
 * but some of the branches return
 * some value.
 *
 * ```
fun dispatch(action: Action): Unit =
when (action) {
is SendTextAction -> returningUuid.unit()
is CreateIssueAction -> returningLong.unit()
}

 * ```
 */
// in order to be able to call it using dot notation
@Suppress("unused")
fun Any?.unit() = Unit


fun <T> List<T>.second(): T {
    if (size > 1)
        throw NoSuchElementException("List does not contain second element.")
    return this[1]
}
