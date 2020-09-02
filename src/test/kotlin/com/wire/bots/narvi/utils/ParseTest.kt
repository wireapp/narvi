package com.wire.bots.narvi.utils

import org.junit.jupiter.api.Test
import pw.forst.tools.katlib.parseJson
import kotlin.test.assertEquals

internal class ParseTest {

    private data class A(val b: String)

    @Test
    fun `try to parse json`() {
        val json = "{\"b\":\"something\"}"
        val parsed = parseJson<A>(json)
        assertEquals(A("something"), parsed)
    }
}
