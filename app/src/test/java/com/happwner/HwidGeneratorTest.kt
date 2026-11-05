package com.happwner

import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class HwidGeneratorTest {
    @Test fun `Android and TV IDs use 16 lowercase hex digits`() {
        listOf("android", "pixel9", "galaxys24", "androidtv").forEach { id ->
            assertTrue(HwidGenerator.generate(id).matches(Regex("[0-9a-f]{16}")))
        }
    }

    @Test fun `Apple IDs use uppercase UUID format`() {
        listOf("iphone11", "iphone15pro", "macbookair").forEach { id ->
            val value = HwidGenerator.generate(id)
            assertTrue(value.matches(Regex("[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}")))
            assertEquals(4, UUID.fromString(value).version())
        }
    }

    @Test fun `Windows IDs use lowercase UUID format`() {
        assertTrue(HwidGenerator.generate("windows10").matches(Regex("[0-9a-f-]{36}")))
    }

    @Test fun `Linux IDs use machine-id shaped 32 hex digits`() {
        assertTrue(HwidGenerator.generate("linux").matches(Regex("[0-9a-f]{32}")))
    }

    @Test fun `explicit generations produce different values`() {
        assertEquals(100, (1..100).map { HwidGenerator.generate("iphone11") }.toSet().size)
    }
}
