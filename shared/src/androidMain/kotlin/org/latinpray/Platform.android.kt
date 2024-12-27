package org.latinpray

import android.content.res.Configuration


class AndroidPlatform : Platform {
    override val osName: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val versionCode: String = "${android.os.Build.VERSION.SDK_INT}"
    override val appName: String
    override val appVersion: String = "1.0"
    override val extraIndent: String = ""
    override val isIOS: Boolean = false

    init {
        val context = AndroidInjector.application.applicationInfo
        appName = context.loadLabel(AndroidInjector.application.packageManager).toString()
        //val version = context.
    }

    override fun isTablet(): Boolean {
        val context = AndroidInjector.application
        val screenSize = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE
    }
}

actual fun getPlatformPriv(): Platform = AndroidPlatform()
