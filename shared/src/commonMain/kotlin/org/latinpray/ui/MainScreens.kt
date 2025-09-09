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

package org.latinpray.ui

import org.jetbrains.compose.resources.StringResource
import org.latinpray.shared.Res
import org.latinpray.shared.about_screen_title
import org.latinpray.shared.bible_settings_title
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.prayer_details_screen_title
import org.latinpray.shared.prayers_screen_title
import org.latinpray.shared.settings_screen_title

enum class MainScreens(val title: StringResource) {
    PrayersScreen(title = Res.string.prayers_screen_title),
    PrayerDetailsScreen(title = Res.string.prayer_details_screen_title),
    SettingsScreen(title = Res.string.settings_screen_title),
    AboutScreen(title = Res.string.about_screen_title),
    HelpScreen(title = Res.string.help_screen_title),
    BibleSettingsScreen(title = Res.string.bible_settings_title)
}

