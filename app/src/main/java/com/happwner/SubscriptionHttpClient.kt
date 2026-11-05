package com.happwner

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/** Shared by manual requests and Bridge so failures cannot become successful subscriptions. */
object SubscriptionHttpClient {
    const val MAX_BODY_BYTES = 16 * 1024 * 1024
    private const val MAX_ERROR_BYTES = 8 * 1024

    data class Response(val status: Int, val body: String, val headers: Map<String, List<String>>) {
        val successful: Boolean get() = status in 200..299
    }

    fun fetch(url: String, hwid: String, ua: String, deviceHeaders: Map<String, String>, timeout: Int = 15000): Response {
        val target = URL(url.trim())
        require(target.protocol == "http" || target.protocol == "https") { "Only HTTP(S) subscriptions are supported" }
        val conn = target.openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "GET"
            conn.connectTimeout = timeout
            conn.readTimeout = timeout
            conn.setRequestProperty("x-hwid", safeHeader(hwid))
            if (ua.isNotBlank()) conn.setRequestProperty("User-Agent", safeHeader(ua))
            deviceHeaders.forEach { (key, value) -> conn.setRequestProperty(key, safeHeader(value)) }
            val status = conn.responseCode
            val headers = conn.headerFields.filterKeys { it != null }
            if (status !in 200..299) {
                val detail = conn.errorStream?.let { readCapped(it, MAX_ERROR_BYTES, truncate = true) }.orEmpty()
                return Response(status, "HTTP $status" + if (detail.isBlank()) "" else "\n$detail", headers)
            }
            return Response(status, readCapped(conn.inputStream, MAX_BODY_BYTES), headers)
        } finally {
            conn.disconnect()
        }
    }

    private fun safeHeader(value: String): String {
        require(value.none { it == '\r' || it == '\n' || it.code < 32 || it.code == 127 }) { "Invalid request header" }
        return value
    }

    internal fun readCapped(input: InputStream, maxBytes: Int, truncate: Boolean = false): String {
        val out = ByteArrayOutputStream()
        val chunk = ByteArray(8192)
        input.use {
            while (true) {
                val n = it.read(chunk, 0, minOf(chunk.size, maxBytes - out.size() + 1))
                if (n < 0) break
                val remaining = maxBytes - out.size()
                if (n > remaining) {
                    if (!truncate) throw IOException("Subscription exceeds the ${maxBytes / 1024 / 1024} MiB size limit")
                    out.write(chunk, 0, remaining)
                    return out.toString("UTF-8") + "\n…"
                }
                out.write(chunk, 0, n)
            }
        }
        return out.toString("UTF-8")
    }
}
