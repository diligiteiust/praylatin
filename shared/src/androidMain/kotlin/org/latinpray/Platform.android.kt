package org.latinpray

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
}

actual fun getPlatformPriv(): Platform = AndroidPlatform()
