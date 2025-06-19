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

import com.russhwolf.settings.Settings
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

interface Platform {
    val osName: String
    val osVersion: String
    val appName: String
    val appVersionCode: String
    val appVersion: String
    val extraIndent: String
    val isIOS: Boolean

    fun isTablet(): Boolean
    fun changeLang(lang: String)
}

expect fun getPlatformPriv(): Platform

expect fun createSettings(): Settings

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