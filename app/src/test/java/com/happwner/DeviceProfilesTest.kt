package com.happwner

import org.junit.Assert.*
import org.junit.Test

class DeviceProfilesTest {
    @Test fun `every preset has a unique ID and belongs to one group`() {
        assertEquals(DeviceProfiles.all.size, DeviceProfiles.all.map { it.id }.toSet().size)
        assertTrue(DeviceProfiles.all.all { it.group in DeviceProfiles.groups })
        assertTrue(DeviceProfiles.groups.all { DeviceProfiles.inGroup(it).isNotEmpty() })
    }

    @Test fun `Apple contains iPhone 11 and Android contains Pixels`() {
        assertTrue(DeviceProfiles.inGroup("apple").any { it.id == "iphone11" && it.os == "iOS" })
        assertTrue(DeviceProfiles.inGroup("android").any { it.id == "pixel9" })
    }

    @Test fun `custom model and OS version override preset metadata`() {
        val headers = RequestDevice.headers("windows", "16", "Pixel", "10", "Dell XPS 13")
        assertEquals("Windows", headers["x-device-os"])
        assertEquals("10", headers["x-ver-os"])
        assertEquals("Dell XPS 13", headers["x-device-model"])
        assertEquals("Happ/4.10.1/Windows", RequestDevice.userAgent("Happ/4.10.1", "windows"))
    }

    @Test fun `iPhone model selects iOS instead of Android metadata`() {
        assertEquals("iPhone 11", RequestDevice.headers("iphone11", "16", "Pixel")["x-device-model"])
        assertEquals("Happ/4.10.1/ios", RequestDevice.userAgent("Happ/4.10.1", "iphone11"))
    }

    @Test fun `v2RayTun built-in platform identifier follows device`() {
        assertEquals("v2raytun/ios", RequestDevice.userAgent("v2raytun/android", "iphone11"))
        assertEquals("v2raytun/windows", RequestDevice.userAgent("v2raytun/android", "windows10"))
    }
}
