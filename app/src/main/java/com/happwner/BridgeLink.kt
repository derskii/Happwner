package com.happwner

import java.net.URLDecoder
import java.net.URLEncoder

object BridgeLink {
    fun create(url: String, hwid: String, ua: String, device: String, osVersion: String, model: String): String {
        val params = linkedMapOf("url" to url, "hwid" to hwid, "ua" to ua, "device" to device,
            "osver" to osVersion, "model" to model)
        return "http://127.0.0.1:8166/" + params.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, "UTF-8")}"
        }
    }

    fun parse(query: String): Map<String, String> = query.split('&').mapNotNull { pair ->
        val index = pair.indexOf('=')
        if (index < 0) null else pair.substring(0, index) to URLDecoder.decode(pair.substring(index + 1), "UTF-8")
    }.toMap()
}
