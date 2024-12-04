package org.latinpray

interface Platform {
    val name: String
    val versionCode: String
}

expect fun getPlatform(): Platform
