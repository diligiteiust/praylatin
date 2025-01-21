/*
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. Look for COPYING file in the top folder.
 *  If not, see http://www.gnu.org/licenses/.
 */

package org.latinpray

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import java.util.Locale


class AndroidPlatform : Platform {
    override val osName: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val osVersion: String = "${android.os.Build.VERSION.SDK_INT}"
    override val appName: String
    override val appVersionCode: String
    override val appVersion: String
    override val extraIndent: String = ""
    override val isIOS: Boolean = false

    init {
        val context = AndroidInjector.application.applicationInfo
        appName = context.loadLabel(AndroidInjector.application.packageManager).toString()
        var pinfo: PackageInfo? = null
        try {
            pinfo = AndroidInjector.application.packageManager.getPackageInfo(AndroidInjector.application.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        appVersionCode = pinfo!!.longVersionCode.toString()
        appVersion = (pinfo.versionName ?: "-1") + " (" + appVersionCode + ")"
    }

    override fun isTablet(): Boolean {
        val context = AndroidInjector.application
        val screenSize = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE
    }

    override fun changeLang(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
    }
}

actual fun getPlatformPriv(): Platform = AndroidPlatform()
