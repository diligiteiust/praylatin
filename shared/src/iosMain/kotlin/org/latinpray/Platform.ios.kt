package org.latinpray

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad

class IOSPlatform: Platform {
    override val osName: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val versionCode: String = UIDevice.currentDevice.systemVersion
    override val appName: String = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleDisplayName") as String? ?: "Pray Latin"
    override val appVersion: String =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String? ?: "0.0.0") +
                " (" + (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as String? ?: "0") + ")"
    override val extraIndent: String = "\t\t"
    override val isIOS = true

    init {
        //println("IOSPlatform: $osName")
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(apiKey = "appl_nltzYgyKKbijoRZvcmgvvsumVPt")
    }

    override fun isTablet(): Boolean {
        return UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad
    }
}

actual fun getPlatformPriv(): Platform = IOSPlatform()
