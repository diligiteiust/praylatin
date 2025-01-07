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

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
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

    override fun changeLang(lang: String) {
        NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang), "AppleLanguages")
    }
}

actual fun getPlatformPriv(): Platform = IOSPlatform()
