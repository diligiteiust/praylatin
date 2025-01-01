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

package org.latinpray.io

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.latinpray.AndroidInjector

// shared/src/androidMain/kotlin/createDataStore.android.kt

//fun createConfigDataStore(context: Context): DataStore<Preferences> = createConfigDataStore(
//    producePath = { context.filesDir.resolve(configStoreFileName).absolutePath }
//)

//fun getDataStore(context: Context): DataStore<Preferences> = getDataStore(
//    producePath = { context.filesDir.resolve(configStoreFileName).absolutePath }
//)

actual fun keyValueStorePath(): String {
    return AndroidInjector.application.applicationContext.filesDir.resolve("datastore/$configStoreFileName").absolutePath
}