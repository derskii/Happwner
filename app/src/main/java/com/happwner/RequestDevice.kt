package com.happwner

/** Explicit request metadata; this does not change the physical device or its HWID. */
object RequestDevice {
    val ids = listOf("android", "androidtv", "ios", "windows", "macos", "linux")

    fun normalize(id: String?): String = id?.takeIf { it in ids } ?: "android"

    fun headers(id: String, androidVersion: String, androidModel: String): Map<String, String> {
        val values = when (normalize(id)) {
            "androidtv" -> mapOf("x-device-os" to "AndroidTV", "x-device-model" to "Android TV")
            "ios" -> mapOf("x-device-os" to "iOS", "x-device-model" to "iPhone")
            "windows" -> mapOf("x-device-os" to "Windows", "x-device-model" to "PC")
            "macos" -> mapOf("x-device-os" to "macOS", "x-device-model" to "Mac")
            "linux" -> mapOf("x-device-os" to "Linux", "x-device-model" to "PC")
            else -> mapOf("x-device-os" to "Android", "x-ver-os" to androidVersion, "x-device-model" to androidModel)
        }
        return values.mapValues { (_, value) -> value.filter { it.code in 32..126 }.trim() }
            .filterValues { it.isNotEmpty() }
    }

    fun userAgent(base: String, id: String): String {
        val clean = base.replace("\r", "").replace("\n", "").trim()
        val platform = when (normalize(id)) {
            "androidtv" -> "AndroidTV"
            "ios" -> "ios"
            "windows" -> "Windows"
            "macos" -> "macOS"
            "linux" -> "Linux"
            else -> "Android"
        }
        // Preserve custom strings; only complete a plain Happ/<version> preset.
        return if (clean.startsWith("Happ/") && clean.count { it == '/' } == 1) "$clean/$platform" else clean
    }
}
