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

import okio.Source

expect val defaultAssetFileProvider: AssetFileProvider

// Common code
expect fun listAssetsInDirectory(directory: String): List<String>

/**
 * Exposes a single method that returns [okio.Source] with which we can read/stream the asset file
 * The path is the assets/filepath of the file stored in resources/assets
 */
fun interface AssetFileProvider {
    fun get(path: String): Source
}
