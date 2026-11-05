package com.happwner

import java.security.SecureRandom
import java.util.Locale
import java.util.UUID

/** Synthetic IDs in common platform formats. Not a real Android ID, IDFV, or hardware attestation. */
object HwidGenerator {
    private val random = SecureRandom()

    fun generate(profileId: String): String = when (DeviceProfiles.find(profileId).os) {
        "Android", "AndroidTV" -> hex(8)
        "Linux" -> hex(16)
        "iOS", "macOS" -> UUID.randomUUID().toString().uppercase(Locale.ROOT)
        else -> UUID.randomUUID().toString()
    }

    private fun hex(size: Int): String {
        val data = ByteArray(size).also(random::nextBytes)
        return data.joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }
    }
}
