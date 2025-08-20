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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.data.offers
import org.latinpray.data.privacy
import org.latinpray.data.terms
import org.latinpray.io.prayersList
import org.latinpray.io.readConfigFromAssets
import org.latinpray.io.readFileFromAssets
import org.latinpray.loc.LocalizedApp
import org.latinpray.shared.Res
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.prayers_screen_title
import org.latinpray.shared.settings_screen_title
import org.latinpray.theme.AppTheme
import org.latinpray.theme.TABLET_CONTENT_FONT_FACTOR
import org.latinpray.theme.TABLET_HEADLINE_FONT_FACTOR
import org.latinpray.theme.TABLET_UI_FONT_FACTOR
import org.latinpray.ui.AboutScreen
import org.latinpray.ui.ContentItem
import org.latinpray.ui.HelpScreen
import org.latinpray.ui.MainScreens
import org.latinpray.ui.PrayerDetailsScreen
import org.latinpray.ui.PrayersListScreen
import org.latinpray.ui.SettingsScreen
import org.latinpray.data.ReadingPlan
import org.latinpray.io.readBibleReadingPlan

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
    return prayersList(mutableListOf<Prayer>(), config).sortedBy { prayer ->
        prayer.langs[config.prayerLang]?.title
    }.toMutableList()
}

@OptIn(ExperimentalSharedTransitionApi::class)
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
    var currentContent: ContentItem? = null
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
        //println("Main scope launched")
        //println("Loading config from yaml...")
//        defConfig = readConfigFromAssets("assets/config.yaml")
//        //println("Loaded config from yaml")
//        val dsConfig = Config(
//            defConfig.uiLang,
//            defConfig.prayerLang,
//            defConfig.secondLang,
//            defConfig.preferTranslation,
//            defConfig.grouping
//        )
//        dsConfig.loadConfigProps()
////        dsConfig.loadConfigProps(getDataStore { keyValueStorePath() })
////        defConfig = dsConfig
////        defConfig.dataStore = getDataStore { keyValueStorePath() }
//        defConfig = dsConfig
//        if (lang != defConfig.uiLang) {
//            lang = defConfig.uiLang
//            getPlatform().changeLang(lang)
//        }
//        fontScale = defConfig.fontScale
        //println("Loaded config from datastore ${defConfig.prayerLang}")
        //println("Loading prayers...")
        helpContent = loadLocalizedContent("assets/help.md", defConfig.uiLang)
        aboutContent = loadLocalizedContent("assets/about.md", defConfig.uiLang)
        privacy = readFileFromAssets("assets/privacy.md")
        terms = readFileFromAssets("assets/terms.md")
        prayers = prayersList(prayers, defConfig).sortedBy { prayer ->
            prayer.langs[defConfig.prayerLang]?.title
        }.toMutableList()
        println("Loaded ${prayers.size} prayers")
        readingPlan = readBibleReadingPlan("assets/bible/annual-plan.yaml", defConfig)
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
                                //animatedVisibilityScope = this,
                                //sharedTransitionScope = sharedTransitionScope,
                                onClick = { content ->
                                    currentContent = content
                                    navController.navigate(MainScreens.PrayerDetailsScreen.name)
                                },
                                navController = navController,
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
                            PrayerDetailsScreen(
                                startContent = currentContent!!,
                                config = defConfig,
                                prayers = prayers,
                                //animatedContentScope = this,
                                //sharedTransitionScope = sharedTransitionScope,
                                goBack = { navController.popBackStack() }
                            )
                        }
                        composable(route = MainScreens.SettingsScreen.name) {
                            SettingsScreen(
                                title = stringResource(Res.string.settings_screen_title),
                                //animatedContentScope = this,
                                //sharedTransitionScope = sharedTransitionScope,
                                config = defConfig,
                                goBack = {
                                            if (reloadPrayersFlag) {
                                                reloadPrayersFlag = false
                                                scope.launch {
                                                    println("Reloading prayers...")
                                                    prayers = reloadPrayers(defConfig)
                                                    //currentContent = prayers.first()
                                                }
                                            }
                                            navController.popBackStack()
                                         },
                                uiLangChange = { config ->
                                    //defConfig = config
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
                                    println("Reload prayers flag set to true...")
                                }
                            )
                        }
                        composable(route = MainScreens.AboutScreen.name) {
                            AboutScreen(
                                content = aboutContent,
                                goBack = { navController.popBackStack() },
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
                                goBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}