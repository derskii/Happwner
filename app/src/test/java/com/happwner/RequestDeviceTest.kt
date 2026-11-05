package com.happwner

import org.junit.Assert.*
import org.junit.Test

class RequestDeviceTest {
    @Test fun `unknown profile falls back to Android`() {
        assertEquals("android", RequestDevice.normalize("unknown"))
        assertEquals("Android", RequestDevice.headers("unknown", "16", "Pixel")["x-device-os"])
    }

    @Test fun `every preset has safe request metadata`() {
        RequestDevice.ids.forEach { id ->
            val headers = RequestDevice.headers(id, "16", "Pixel\r\nTest")
            assertTrue(headers.containsKey("x-device-os"))
            assertTrue(headers.values.none { '\r' in it || '\n' in it })
        }
    }

    @Test fun `custom agents are not rewritten`() {
        assertEquals("Custom/1.0", RequestDevice.userAgent("Custom/1.0", "windows"))
        assertEquals("Happ/4.10.1/Windows/custom", RequestDevice.userAgent("Happ/4.10.1/Windows/custom", "ios"))
    }

    @Test fun `Happ preset follows selected platform`() {
        assertEquals("Happ/4.10.1/Windows", RequestDevice.userAgent("Happ/4.10.1", "windows"))
    }
}
