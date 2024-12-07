package org.latinpray

interface Platform {
    val osName: String
    val versionCode: String
    val appName: String
    val appVersion: String
}

expect fun getPlatform(): Platform
