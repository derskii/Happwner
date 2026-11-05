package com.happwner

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class LinkConverterTest {
    private fun config(index: Int): JSONObject = JSONObject("""
        {"remarks":"Server $index", "outbounds":[{"protocol":"vless","settings":{"vnext":[{
        "address":"server$index.example","port":443,"users":[{"id":"00000000-0000-0000-0000-000000000001"}]}]}}]}
    """.trimIndent())

    @Test fun `500 pretty printed JSON configs convert without losing entries`() {
        val input = JSONArray().apply { repeat(500) { put(config(it)) } }.toString(2)
        val output = LinkConverter.convert(input, jsonToUri = true, tryBase64 = false)
        assertEquals(500, output.lineSequence().count())
        assertTrue(output.lines().all { it.startsWith("vless://") })
        assertTrue(output.contains("server499.example"))
    }

    @Test fun `1000 compact configs convert without truncation`() {
        val input = JSONArray().apply { repeat(1000) { put(config(it)) } }.toString()
        val output = LinkConverter.convert(input, jsonToUri = true, tryBase64 = false)
        assertEquals(1000, output.lineSequence().count())
    }

    @Test fun `pretty printed single object is converted as a whole`() {
        assertTrue(LinkConverter.convert(config(1).toString(2), true, false).startsWith("vless://"))
    }

    @Test fun `invalid outbound does not hide a later valid one`() {
        val cfg = config(1)
        val valid = cfg.getJSONArray("outbounds").getJSONObject(0)
        cfg.put("outbounds", JSONArray().put("broken").put(valid))
        assertTrue(LinkConverter.convert(cfg.toString(), true, false).startsWith("vless://"))
    }

    @Test fun `malformed input is preserved`() {
        val input = "{not valid json"
        assertEquals(input, LinkConverter.convert(input, true, false))
    }

    @Test fun `raw export mode preserves JSON`() {
        val input = JSONArray().put(config(1)).toString(2)
        assertEquals(input, LinkConverter.convert(input, false, false))
    }

    @Test(expected = IllegalArgumentException::class) fun `deep JSON is rejected before parser stack overflow`() {
        LinkConverter.convert("[".repeat(500) + "0" + "]".repeat(500), true, false)
    }

    @Test fun `braces inside strings do not count as nesting`() {
        JsonSafety.checkDepth(JSONObject().put("name", "[".repeat(500) + "\\\"}").toString())
    }
}
