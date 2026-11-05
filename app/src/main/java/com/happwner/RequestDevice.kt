package com.happwner

/** Explicit request metadata; this does not change the physical device or its HWID. */
object RequestDevice {
    val ids = DeviceProfiles.all.map { it.id }

    fun normalize(id: String?): String = id?.takeIf { it in ids } ?: "android"

    fun headers(id: String, androidVersion: String, androidModel: String,
                versionOverride: String = "", modelOverride: String = ""): Map<String, String> {
        val profile = DeviceProfiles.find(id)
        val model = modelOverride.ifBlank { if (profile.id == "android") androidModel else profile.model }
        val version = versionOverride.ifBlank { if (profile.id == "android") androidVersion else profile.version }
        val values = mapOf("x-device-os" to profile.os, "x-ver-os" to version, "x-device-model" to model)
        return values.mapValues { (_, value) -> value.filter { it.code in 32..126 }.trim() }
            .filterValues { it.isNotEmpty() }
    }

    fun userAgent(base: String, id: String): String {
        val clean = base.replace("\r", "").replace("\n", "").trim()
        val os = DeviceProfiles.find(id).os
        val platform = if (os == "iOS") "ios" else os
        // Preserve custom strings; only complete a plain Happ/<version> preset.
        return when {
            clean.startsWith("Happ/") && clean.count { it == '/' } == 1 -> "$clean/$platform"
            clean.startsWith("v2raytun/") && clean.substringAfter('/') in listOf("android", "ios", "windows", "macos", "linux") ->
                "v2raytun/" + when (os) { "AndroidTV" -> "android"; else -> os.lowercase(java.util.Locale.ROOT) }
            else -> clean
        }
    }
}
