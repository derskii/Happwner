package com.happwner

/** Never lay out an unbounded provider response in an Android TextView. Export stays lossless. */
object SubscriptionPreview {
    const val PAGE_CHARS = 15000

    fun end(text: String, requested: Int): Int {
        var end = requested.coerceIn(0, text.length)
        if (end > 0 && end < text.length && text[end - 1].isHighSurrogate() && text[end].isLowSurrogate()) end--
        return end
    }

    fun fileName(text: String): String =
        if (text.trimStart().let { it.startsWith("{") || it.startsWith("[") }) "subscription.json" else "subscription.txt"
}
