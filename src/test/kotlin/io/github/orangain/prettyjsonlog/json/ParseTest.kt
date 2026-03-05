package io.github.orangain.prettyjsonlog.json

import junit.framework.TestCase

class ParseTest : TestCase() {
    fun testParseJsonLine() {
        val result = parseJson("""{"key": "value"}""")
        assertNotNull(result)
        val (node, rest) = result!!
        assertEquals("""{"key":"value"}""", node.toString())
        assertEquals("", rest)
    }

    fun testParseJsonLineWithSpaces() {
        val result = parseJson(""" {"key": "value"}  """)
        assertNotNull(result)
        val (node, rest) = result!!
        assertEquals("""{"key":"value"}""", node.toString())
        assertEquals("  ", rest)
    }

    fun testParseBrokenJsonLine() {
        val result = parseJson("""{"key": "value" """)
        assertNull(result)
    }

    fun testParseJsonLineWithContainerTimestampPrefix() {
        val result = parseJson("""2026-03-05T13:29:00.097204551Z {"key": "value"}""")
        assertNotNull(result)
        val (node, rest) = result!!
        assertEquals("""{"key":"value"}""", node.toString())
        assertEquals("", rest)
    }

    fun testParseJsonLineWithContainerTimestampPrefixAndTrailingNewline() {
        val result = parseJson("2026-03-05T13:29:00.097204551Z {\"key\": \"value\"}\n")
        assertNotNull(result)
        val (node, rest) = result!!
        assertEquals("""{"key":"value"}""", node.toString())
        assertEquals("\n", rest)
    }

    fun testParseAnonymizedLineWithContainerTimestampPrefix() {
        val result = parseJson(
            """2026-03-05T13:29:00.097204551Z {"app":"ANONYMIZED_SERVICE","ts":"2026-03-05T13:29:00.096Z","logger":"ANONYMIZED_PACKAGE.ElevationDispatcher","level":"DEBUG","class":"ANONYMIZED_PACKAGE.ElevationDispatcher","method":"getElevations","file":"ElevationDispatcher.java","line":83,"thread":"http-nio-8090-exec-N","traceId":"XXXX-XXXX-XXXX-XXXX","spanId":"XXXX-XXXX-XXXX-XXXX","elevations.size() ":53,"notNullCoordinates.size()":53,"msg":"done Getting elevations"}"""
        )
        assertNotNull(result)
        val (node, rest) = result!!
        assertEquals("done Getting elevations", node.get("msg").asText())
        assertEquals("", rest)
    }

    fun testRejectNonTimestampPrefix() {
        val result = parseJson("INFO {\"key\": \"value\"}")
        assertNull(result)
    }

    fun testParseEmptyString() {
        val result = parseJson("")
        assertNull(result)
    }
}
