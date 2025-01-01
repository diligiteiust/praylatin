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
