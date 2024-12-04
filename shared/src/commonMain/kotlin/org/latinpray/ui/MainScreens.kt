package org.latinpray.ui

import org.jetbrains.compose.resources.StringResource
import org.latinpray.shared.Res
import org.latinpray.shared.about_screen_title
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.prayer_details_screen_title
import org.latinpray.shared.prayers_screen_title
import org.latinpray.shared.settings_screen_title

enum class MainScreens(val title: StringResource) {
    PrayersScreen(title = Res.string.prayers_screen_title),
    PrayerDetailsScreen(title = Res.string.prayer_details_screen_title),
    SettingsScreen(title = Res.string.settings_screen_title),
    AboutScreen(title = Res.string.about_screen_title),
    HelpScreen(title = Res.string.help_screen_title)
}

