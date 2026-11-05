package com.happwner

/** Example request identities, not claims about installed hardware or the latest OS release. */
object DeviceProfiles {
    data class Profile(val id: String, val group: String, val model: String, val os: String, val version: String)
    val groups = listOf("android", "apple", "windows", "linux")
    val all = listOf(
        Profile("android", "android", "This Android device", "Android", ""),
        Profile("pixel7", "android", "Google Pixel 7", "Android", "14"),
        Profile("pixel8", "android", "Google Pixel 8", "Android", "15"),
        Profile("pixel9", "android", "Google Pixel 9", "Android", "16"),
        Profile("pixel9pro", "android", "Google Pixel 9 Pro", "Android", "16"),
        Profile("galaxys24", "android", "Samsung Galaxy S24", "Android", "15"),
        Profile("galaxys25", "android", "Samsung Galaxy S25", "Android", "16"),
        Profile("androidtv", "android", "Android TV", "AndroidTV", "12"),
        Profile("shield", "android", "NVIDIA SHIELD Android TV", "AndroidTV", "11"),
        Profile("ios", "apple", "iPhone", "iOS", "18.0"),
        Profile("iphone11", "apple", "iPhone 11", "iOS", "18.0"),
        Profile("iphone12", "apple", "iPhone 12", "iOS", "18.0"),
        Profile("iphone13", "apple", "iPhone 13", "iOS", "18.0"),
        Profile("iphone14pro", "apple", "iPhone 14 Pro", "iOS", "18.0"),
        Profile("iphone15", "apple", "iPhone 15", "iOS", "18.0"),
        Profile("iphone15pro", "apple", "iPhone 15 Pro", "iOS", "18.0"),
        Profile("iphone16", "apple", "iPhone 16", "iOS", "18.0"),
        Profile("ipad", "apple", "iPad Air (M2)", "iOS", "18.0"),
        Profile("macos", "apple", "Mac", "macOS", "15.0"),
        Profile("macbookair", "apple", "MacBook Air (M3)", "macOS", "15.0"),
        Profile("macbookpro", "apple", "MacBook Pro (M4)", "macOS", "15.0"),
        Profile("windows", "windows", "PC", "Windows", "11"),
        Profile("windows10", "windows", "PC", "Windows", "10"),
        Profile("surface", "windows", "Microsoft Surface Laptop 5", "Windows", "11"),
        Profile("thinkpad", "windows", "Lenovo ThinkPad T14", "Windows", "11"),
        Profile("linux", "linux", "PC", "Linux", "6.8"),
        Profile("ubuntu", "linux", "Ubuntu PC", "Linux", "6.8"),
        Profile("fedora", "linux", "Fedora PC", "Linux", "6.11")
    )

    fun find(id: String?): Profile = all.firstOrNull { it.id == id } ?: all.first()
    fun inGroup(group: String): List<Profile> = all.filter { it.group == group }
}
