package org.latinpray

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
