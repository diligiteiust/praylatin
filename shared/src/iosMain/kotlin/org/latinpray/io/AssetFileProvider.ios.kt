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

import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle

// bundled resources are kept in compose-resources path in iOS framework.
// See org.jetbrains.compose.resources.Resource.ios.kt
// This is only possible when we add compose resources dependency. Otherwise, I have not found an easy way to do it.
actual val defaultAssetFileProvider: AssetFileProvider = AssetFileProvider { path ->
    val assetFile = NSBundle.mainBundle.resourcePath + "/compose-resources/" + path
    FileSystem.SYSTEM.source(assetFile.toPath())
}

actual fun listAssetsInDirectory(directory: String): List<String> {
    val bundle = NSBundle.mainBundle.resourcePath + "/compose-resources/" + directory
    val list = FileSystem.SYSTEM.list(bundle.toPath())
    return list.map { it.name }
}

