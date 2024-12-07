package org.latinpray

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val osName: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val versionCode: String = UIDevice.currentDevice.systemVersion
    override val appName: String = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleDisplayName") as String? ?: "Pray Latin"
    override val appVersion: String =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String? ?: "0.0.0") +
                " (" + (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as String? ?: "0") + ")"
}

actual fun getPlatform(): Platform = IOSPlatform()
