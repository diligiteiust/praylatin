package org.latinpray

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

interface Platform {
    val osName: String
    val versionCode: String
    val appName: String
    val appVersion: String
    val extraIndent: String
    val isIOS: Boolean

    fun isTablet(): Boolean
}

expect fun getPlatformPriv(): Platform

private lateinit var platform: Platform

@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()

@OptIn(InternalCoroutinesApi::class)
fun getPlatform(): Platform {
    synchronized(lock) {
        if (!::platform.isInitialized) {
            platform = getPlatformPriv()
        }
        return platform
    }
}