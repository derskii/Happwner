package com.happwner

import org.junit.Assert.*
import org.junit.Test

class BridgeLinkTest {
    @Test fun `full identity round trips through encoded Bridge URL`() {
        val url = "https://example.com/sub?a=hello+world&b=12#part"
        val link = BridgeLink.create(url, "a+b=123", "Happ/4.10.1", "iphone11", "18.1", "iPhone 11")
        val params = BridgeLink.parse(link.substringAfter("8166/"))
        assertEquals(url, params["url"])
        assertEquals("a+b=123", params["hwid"])
        assertEquals("iPhone 11", params["model"])
        assertEquals("18.1", params["osver"])
        assertEquals("iphone11", params["device"])
        assertEquals("Happ/4.10.1", params["ua"])
    }

    @Test fun `legacy links still parse and select Android`() {
        val params = BridgeLink.parse("url=https%3A%2F%2Fexample.com&hwid=123&ua=Happ%2F3.0")
        assertEquals("https://example.com", params["url"])
        assertEquals("android", RequestDevice.normalize(params["device"]))
    }
}
