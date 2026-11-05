package com.happwner

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class SubscriptionHttpClientTest {
    @Test fun `HTTP 500 JSON is an error and not a subscription`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(500).setBody("{\"error\":\"upstream unavailable\"}"))
            val response = SubscriptionHttpClient.fetch(server.url("/sub").toString(), "", "", emptyMap())
            assertEquals(500, response.status)
            assertFalse(response.successful)
            assertTrue(response.body.contains("upstream unavailable"))
        }
    }

    @Test fun `headers and UTF-8 body survive successful requests`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("подписка 🌍").addHeader("profile-title", "example"))
            val response = SubscriptionHttpClient.fetch(server.url("/sub").toString(), "my-device",
                RequestDevice.userAgent("Happ/4.10.1", "ios"), RequestDevice.headers("ios", "16", "Pixel"))
            assertTrue(response.successful)
            assertEquals("подписка 🌍", response.body)
            val request = server.takeRequest()
            assertEquals("my-device", request.getHeader("x-hwid"))
            assertEquals("iOS", request.getHeader("x-device-os"))
            assertEquals("Happ/4.10.1/ios", request.getHeader("User-Agent"))
        }
    }

    @Test fun `all successful HTTP statuses are accepted`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(201).setBody("vless://example"))
            assertTrue(SubscriptionHttpClient.fetch(server.url("/").toString(), "", "", emptyMap()).successful)
        }
    }

    @Test fun `large error bodies are bounded`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(500).setBody("x".repeat(20000)))
            val response = SubscriptionHttpClient.fetch(server.url("/").toString(), "", "", emptyMap())
            assertFalse(response.successful)
            assertTrue(response.body.length < 8300)
        }
    }

    @Test(expected = IllegalArgumentException::class) fun `non HTTP URLs are rejected`() {
        SubscriptionHttpClient.fetch("file:///etc/passwd", "", "", emptyMap())
    }

    @Test(expected = IllegalArgumentException::class) fun `header injection is rejected`() {
        SubscriptionHttpClient.fetch("https://example.com", "id\r\nInjected: true", "", emptyMap())
    }

    @Test fun `exact body limit is accepted`() {
        assertEquals("abcd", SubscriptionHttpClient.readCapped(ByteArrayInputStream("abcd".toByteArray()), 4))
    }

    @Test(expected = IOException::class) fun `oversized bodies are rejected`() {
        SubscriptionHttpClient.readCapped(ByteArrayInputStream("abcde".toByteArray()), 4)
    }
}
