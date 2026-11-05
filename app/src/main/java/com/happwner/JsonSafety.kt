package com.happwner

object JsonSafety {
    /** Bound parser recursion without counting braces inside quoted strings. */
    fun checkDepth(text: String, maxDepth: Int = 128) {
        var depth = 0
        var quoted = false
        var escaped = false
        var quote = '"'
        for (char in text) {
            if (quoted) {
                when {
                    escaped -> escaped = false
                    char == '\\' -> escaped = true
                    char == quote -> quoted = false
                }
            } else when (char) {
                '"', '\'' -> { quoted = true; quote = char }
                '{', '[' -> {
                    depth++
                    require(depth <= maxDepth) { "JSON nesting exceeds $maxDepth levels" }
                }
                '}', ']' -> depth = maxOf(0, depth - 1)
            }
        }
    }
}
