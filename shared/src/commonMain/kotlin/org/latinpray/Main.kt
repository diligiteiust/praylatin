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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.revenuecat.purchases.kmp.Purchases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.HIDE_TAG
import org.latinpray.data.Prayer
import org.latinpray.data.ReadingPlan
import org.latinpray.data.allTags
import org.latinpray.data.offers
import org.latinpray.data.privacy
import org.latinpray.data.terms
import org.latinpray.io.loadContent
import org.latinpray.io.prayersList
import org.latinpray.io.readConfigFromAssets
import org.latinpray.io.readFileFromAssets
import org.latinpray.loc.LocalizedApp
import org.latinpray.shared.Res
import org.latinpray.shared.bible_settings_title
import org.latinpray.shared.daily_prayers
import org.latinpray.shared.favorite_prayers
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.prayers_screen_title
import org.latinpray.shared.settings_screen_title
import org.latinpray.shared.today_and_now
import org.latinpray.theme.AppTheme
import org.latinpray.theme.TABLET_CONTENT_FONT_FACTOR
import org.latinpray.theme.TABLET_HEADLINE_FONT_FACTOR
import org.latinpray.theme.TABLET_UI_FONT_FACTOR
import org.latinpray.ui.AboutScreen
import org.latinpray.ui.BibleSettings
import org.latinpray.ui.ContentItem
import org.latinpray.ui.HelpScreen
import org.latinpray.ui.MainScreens
import org.latinpray.ui.OnResume
import org.latinpray.ui.PrayerDetailsScreen
import org.latinpray.ui.PrayersListScreen
import org.latinpray.ui.SettingsScreen
import org.latinpray.ui.untilNextFullHour
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun loadLocalizedContent(file: String, lang: String): String {
    val f = file.substringBefore('.') + '-' + lang + "." + file.substringAfter('.')
    //println("reading file: $f")
    try {
        return readFileFromAssets(f).replace('\n', ' ').replace("<p>", "\n   \n")
    } catch (_: Exception) {
        return readFileFromAssets(file).replace('\n', ' ').replace("<p>", "\n   \n")
    }
}

fun reloadPrayers(config: Config): MutableList<Prayer> {
    //println("Reloading prayers...")
    val result = prayersList(mutableListOf<Prayer>(), config).sortedBy { prayer ->
        prayer.langs[config.prayerLang]?.title
    }.toMutableList()
    return result
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalTime::class)
@Composable
fun Main() {
    //println("Main")
    // Initialize platform-specific data
    getPlatform()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val defConfig: Config = readConfigFromAssets("assets/config.yaml")
    //println("Loaded config from yaml")
    //var prayers by remember { mutableStateOf(samplePrayers.toMutableList()) }
    var prayers by remember { mutableStateOf(mutableListOf<Prayer>()) }
    var readingPlan: ReadingPlan? by remember { mutableStateOf(null) }
    var currentContent: ContentItem? by remember { mutableStateOf(null) }
    defConfig.prayersChangedCallback = {
        scope.launch {
            //println("Prayers changed")
            prayers = reloadPrayers(defConfig)
            //currentPrayer = prayers.first()
        }
    }
    var helpContent by remember { mutableStateOf("") }
    var aboutContent by remember { mutableStateOf("") }
    var lang: String by remember { mutableStateOf(defConfig.uiLang) }
    var fontScale by remember { mutableStateOf(defConfig.fontScale) }

    //defConfig = readConfigFromAssets("assets/config.yaml")
    defConfig.loadConfigProps()
    //println("Loaded config from datastore ${defConfig.prayerLang}")
    if (lang != defConfig.uiLang) {
        lang = defConfig.uiLang
        getPlatform().changeLang(lang)
    }
    fontScale = defConfig.fontScale

    scope.launch {
        helpContent = loadLocalizedContent("assets/help.md", defConfig.uiLang)
        aboutContent = loadLocalizedContent("assets/about.md", defConfig.uiLang)
        privacy = readFileFromAssets("assets/privacy.md")
        terms = readFileFromAssets("assets/terms.md")
        prayers = prayersList(prayers, defConfig).sortedBy { prayer ->
            prayer.langs[defConfig.prayerLang]?.title
        }.toMutableList()
        println("Loaded ${prayers.size} prayers")
        //readingPlan = readBibleReadingPlan("assets/bible/annual-plan.yaml", defConfig)
        readingPlan = loadContent("assets/bible/annual-plan.yaml")
        println("Loaded reading plan: ${readingPlan?.name}")
        //println("Loaded reading plan")
        //currentPrayer = prayers.first()
        //if (getPlatform().isIOS) {
        Purchases.sharedInstance.getOfferings(
            onError = { error ->
                // An error occurred
                println("Error: $error")
                //Text(text = "Error: $error")
            },
            onSuccess = { offerings ->
                offerings.current?.availablePackages?.takeUnless { it.isEmpty() }?.let { it ->
                    offers = it
                    println("Offers: $offers")
                    offers!!.forEach { offer ->
                        println("Offer title: ${offer.storeProduct.title}, description: ${offer.storeProduct.id}, price: ${offer.storeProduct.price.formatted}")
                    }
                    // Display packages for sale
                }
            }
        )
        //}
    }

    var currentHour by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour ) }
    scope.launch {
        while (true) {
            delay(untilNextFullHour("Prayers List"))
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            //println("Checking time: ${now.hour}, previous: ${currentHour}")
            if (now.hour != currentHour) {
                currentHour = now.hour
                //println("New time: ${currentHour}")
            }
        }
    }

    OnResume {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        //println("OnResume, Checking time: ${now.hour}, previous: ${currentHour}")
        if (now.hour != currentHour) {
            currentHour = now.hour
            //println("OnResume, New time: ${currentHour}")
        }
    }
    val dailyPrayersStr = stringResource(Res.string.daily_prayers)
    val favoritePrayersStr = stringResource(Res.string.favorite_prayers)
    val todayAndNowStr = stringResource(Res.string.today_and_now)


    val groupedPrayers: MutableList<Any> = remember(prayers, defConfig, currentHour) {
        val gp = mutableListOf<Any>()
        if (defConfig.grouping) {
            val tags = mutableSetOf<String>()
            prayers.forEach { prayer ->
                if ((prayer.langs[defConfig.prayerLang] != null && prayer.langs[defConfig.prayerLang]?.tags != null)
                    || (prayer.langs[defConfig.secondLang] != null && prayer.langs[defConfig.secondLang]?.tags != null)
                ) {
                    val tmp_tags = mutableSetOf<String>()
                    if (prayer.langs[defConfig.prayerLang]?.tags != null) {
                        tmp_tags.addAll(prayer.langs[defConfig.prayerLang]?.tags!!)
                    } else if (prayer.langs[defConfig.secondLang]?.tags != null) {
                        tmp_tags.addAll(prayer.langs[defConfig.secondLang]?.tags!!)
                    }

                    tmp_tags.forEach { tag ->
                        tags.add(allTags.getTagForLanguage(defConfig.uiLang, tag))
                    }
                    //tags.addAll(prayer.langs[config.prayerLang]?.tags!!)
                }
            }
            tags.remove(HIDE_TAG)
            if (defConfig.todayAndNow) {
                gp.add(todayAndNowStr)
                if (defConfig.biblePlan) {
                    runBlocking {
                        val bible = readingPlan?.bibleForToday(defConfig)
                        bible?.let {
                            gp.add(ContentItem(it, it.prayedToday(), todayAndNowStr))
                        }
                    }
                }
                prayers.forEach { prayer ->
                    //println("Checking prayer ${prayer.name} for tag $tag with tags ${prayer.langs[config.prayerLang]?.tags} or ${prayer.langs[config.secondLang]?.tags}")
                    if ((prayer.langs[defConfig.prayerLang] != null || prayer.langs[defConfig.secondLang] != null)
                        && prayer.isTodayAndNow(currentHour)
                    ) {
                        gp.add(ContentItem(prayer, prayer.prayedToday(), todayAndNowStr))
                        //println("Added 1st prayer ${prayer.name} to group: $tag")
                    }
                }

            }
            if (defConfig.dailyPrayers.isNotEmpty()) {
                gp.add(dailyPrayersStr)
                var lastPr: ContentItem? = null
                defConfig.dailyPrayers.forEach { prayer ->
                    prayers.firstOrNull { it.name == prayer }?.let { pr ->
                        val contItem = ContentItem(pr, pr.prayedToday(), dailyPrayersStr)
                        lastPr?.let { lPr ->
                            lPr.nextContent = contItem
                            contItem.prevContent = lPr
                        }
                        lastPr = contItem
                        gp.add(contItem)
                    }
                }
            }
            if (defConfig.favorites.isNotEmpty()) {
                gp.add(favoritePrayersStr)
                defConfig.favorites.forEach { prayer ->
                    prayers.firstOrNull { it.name == prayer }?.let {
                        gp.add(ContentItem(it, false, favoritePrayersStr))
                    }
                }
            }
            tags.sorted().forEach { tag ->
                gp.add(tag)
                prayers.forEach { prayer ->
                    //println("Checking prayer ${prayer.name} for tag $tag with tags ${prayer.langs[config.prayerLang]?.tags} or ${prayer.langs[config.secondLang]?.tags}")
                    if ((prayer.langs[defConfig.prayerLang] != null)
                        && (prayer.langs[defConfig.prayerLang]?.tags?.contains(allTags.getTagForLanguage(defConfig.prayerLang, tag)) == true)
                        && (prayer.langs[defConfig.prayerLang]?.tags?.contains(HIDE_TAG) == false)
                    ) {
                        gp.add(ContentItem(prayer, false, tag))
                        //println("Added 1st prayer ${prayer.name} to group: $tag")
                    } else if ((prayer.langs[defConfig.secondLang] != null)
                        && (prayer.langs[defConfig.secondLang]?.tags?.contains(allTags.getTagForLanguage(defConfig.secondLang, tag)) == true)
                        && prayer.langs[defConfig.secondLang]?.tags?.contains(HIDE_TAG) == false
                    ) {
                        gp.add(ContentItem(prayer, false, tag))
                        //println("Added 2nd prayer ${prayer.name} to group: $tag")
                    }
                }
            }
            gp
        } else {
            prayers.toMutableList()
        }
    }

    val uiFontFactor = if (getPlatform().isTablet()) TABLET_UI_FONT_FACTOR else 1.0f
    val headlineFontFactor = if (getPlatform().isTablet()) TABLET_HEADLINE_FONT_FACTOR else 1.0f
    val contentFontFactor = if (getPlatform().isTablet()) TABLET_CONTENT_FONT_FACTOR else 1.0f
    var reloadPrayersFlag = false

    AppTheme(
        uiFontFactor = uiFontFactor * fontScale,
        headlineFontFactor = headlineFontFactor * fontScale,
        contentFontFactor = contentFontFactor * fontScale
    ) {
        LocalizedApp(
            language = lang
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                SharedTransitionLayout {
                    val sharedTransitionScope = this
                    NavHost(
                        navController = navController,
                        startDestination = MainScreens.PrayersScreen.name,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = MainScreens.PrayersScreen.name) {
                            PrayersListScreen(
                                title = stringResource(Res.string.prayers_screen_title),
                                prayers = prayers,
                                config = defConfig,
                                onClick = { content ->
                                    //println("\n\nClicked prayer")
                                    currentContent = content
                                    //println("Selected prayer")
                                    //println("Selected prayer: ${content.content.name}")
                                    navController.navigate(MainScreens.PrayerDetailsScreen.name)
                                },
                                navController = navController,
                                groupedPrayers = groupedPrayers,
                                fontChange = { scale ->
                                    fontScale += scale
                                    scope.launch {
                                        defConfig.saveFontScale(fontScale)
                                    }
                                },
                                readingPlan = readingPlan

                            )
                        }
                        composable(route = MainScreens.PrayerDetailsScreen.name) {
                            if (currentContent == null) {
                                navController.navigate(MainScreens.PrayersScreen.name)
                            } else {
                                //println("Prayer details screen: ${currentContent?.content?.name}")
                                PrayerDetailsScreen(
                                    startContent = currentContent!!,
                                    config = defConfig,
                                    prayers = prayers,
                                    goBack = {
                                        if (!navController.popBackStack()) {
                                            navController.navigate(MainScreens.PrayersScreen.name)
                                        }
                                    }
                                )
                            }
                        }
                        composable(route = MainScreens.SettingsScreen.name) {
                            SettingsScreen(
                                title = stringResource(Res.string.settings_screen_title),
                                config = defConfig,
                                goBack = {
                                    if (reloadPrayersFlag) {
                                        reloadPrayersFlag = false
                                        scope.launch {
                                            //println("Reloading prayers...")
                                            prayers = reloadPrayers(defConfig)
                                        }
                                    }
                                    if (!navController.popBackStack()) {
                                        navController.navigate(MainScreens.PrayersScreen.name)
                                    }
                                },
                                uiLangChange = { config ->
                                    lang = defConfig.uiLang
                                    getPlatform().changeLang(lang)
                                    println("New lang: $lang")
                                    helpContent =
                                        loadLocalizedContent("assets/help.md", defConfig.uiLang)
                                    aboutContent =
                                        loadLocalizedContent("assets/about.md", defConfig.uiLang)
                                },
                                reloadPrayers = { config ->
                                    //defConfig = config
                                    reloadPrayersFlag = true
                                    //println("Reload prayers flag set to true...")
                                }
                            )
                        }
                        composable(route = MainScreens.BibleSettingsScreen.name) {
                            BibleSettings(
                                title = stringResource(Res.string.bible_settings_title),
                                config = defConfig,
                                goBack = {
                                    if (reloadPrayersFlag) {
                                        reloadPrayersFlag = false
                                        scope.launch {
                                            //println("Reloading prayers...")
                                            prayers = reloadPrayers(defConfig)
                                        }
                                    }
                                    if (!navController.popBackStack()) {
                                        navController.navigate(MainScreens.PrayersScreen.name)
                                    }
                                },
                                reloadPrayers = { config ->
                                    reloadPrayersFlag = true
                                    //println("Reload prayers flag set to true...")
                                }
                            )
                        }
                        composable(route = MainScreens.AboutScreen.name) {
                            AboutScreen(
                                content = aboutContent,
                                goBack = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate(MainScreens.PrayersScreen.name)
                                    }
                                },
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                        composable(route = MainScreens.HelpScreen.name) {
                            HelpScreen(
                                title = stringResource(Res.string.help_screen_title),
                                content = helpContent,
                                config = defConfig,
                                //animatedContentScope = this,
                                sharedTransitionScope = sharedTransitionScope,
                                goBack = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate(MainScreens.PrayersScreen.name)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}