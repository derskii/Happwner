package com.happwner

import org.junit.Assert.*
import org.junit.Test

class SubscriptionPreviewTest {
    @Test fun `preview is bounded while original data is preserved`() {
        val text = "x".repeat(500000)
        assertEquals(15000, SubscriptionPreview.end(text, SubscriptionPreview.PAGE_CHARS))
        assertEquals(500000, text.length)
    }

    @Test fun `page boundaries do not split emoji`() {
        val text = "abc🌍def"
        assertEquals(3, SubscriptionPreview.end(text, 4))
        assertEquals(5, SubscriptionPreview.end(text, 5))
    }

    @Test fun `file extension matches raw JSON or links`() {
        assertEquals("subscription.json", SubscriptionPreview.fileName("\n [ {} ]"))
        assertEquals("subscription.txt", SubscriptionPreview.fileName("vless://example"))
    }
}
