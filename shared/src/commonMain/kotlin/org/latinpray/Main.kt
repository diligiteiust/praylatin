package org.latinpray

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
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
import org.latinpray.data.offers
import org.latinpray.data.sampleConfig
import org.latinpray.data.samplePrayers
import org.latinpray.io.getDataStore
import org.latinpray.io.keyValueStorePath
import org.latinpray.io.prayersList
import org.latinpray.io.readConfigFromAssets
import org.latinpray.io.readFileFromAssets
import org.latinpray.shared.Res
import org.latinpray.shared.about_screen_title
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.prayers_screen_title
import org.latinpray.shared.settings_screen_title
import org.latinpray.theme.AppTheme
import org.latinpray.theme.TABLET_CONTENT_FONT_FACTOR
import org.latinpray.theme.TABLET_HEADLINE_FONT_FACTOR
import org.latinpray.theme.TABLET_UI_FONT_FACTOR
import org.latinpray.ui.AboutScreen
import org.latinpray.ui.HelpScreen
import org.latinpray.ui.MainScreens
import org.latinpray.ui.PrayerDetailsScreen
import org.latinpray.ui.PrayersListScreen
import org.latinpray.ui.SettingsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Main() {
    // Initialize platform-specific data
    getPlatform()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var defConfig by remember { mutableStateOf(sampleConfig) }
    var prayers by remember { mutableStateOf(samplePrayers.toMutableList()) }
    var currentPrayer = prayers.first()
    var helpContent by remember { mutableStateOf("") }
    var aboutContent by remember { mutableStateOf("") }

    scope.launch {
        //println("Loading config from yaml...")
        defConfig = readConfigFromAssets("assets/config.yaml")
        //println("Loaded config from yaml")
        val dsConfig = Config(
            defConfig.uiLang,
            defConfig.prayerLang,
            defConfig.secondLang,
            defConfig.preferTranslation,
            defConfig.grouping
        )
        dsConfig.loadConfigProps(getDataStore { keyValueStorePath() })
        defConfig = dsConfig
        defConfig.dataStore = getDataStore { keyValueStorePath() }
        //println("Loaded config from datastore ${defConfig.prayerLang}")
        //println("Loading prayers...")
        helpContent = readFileFromAssets("assets/help.md")
        aboutContent = readFileFromAssets("assets/about.md")
        prayers = prayersList(prayers, defConfig).sortedBy { prayer ->
            prayer.langs[defConfig.prayerLang]?.title
        }.toMutableList()
        println("Loaded ${prayers.size} prayers")
        currentPrayer = prayers.first()
        if (getPlatform().isIOS) {
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
        }
    }

    val uiFontFactor = if (getPlatform().isTablet()) TABLET_UI_FONT_FACTOR else 1.0f
    val headlineFontFactor = if  (getPlatform().isTablet()) TABLET_HEADLINE_FONT_FACTOR else 1.0f
    val contentFontFactor = if  (getPlatform().isTablet()) TABLET_CONTENT_FONT_FACTOR else 1.0f

    AppTheme(
        uiFontFactor = uiFontFactor,
        headlineFontFactor = headlineFontFactor,
        contentFontFactor = contentFontFactor
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
                            animatedVisibilityScope = this,
                            sharedTransitionScope = sharedTransitionScope,
                            onClick = { prayer ->
                                currentPrayer = prayer
                                navController.navigate(MainScreens.PrayerDetailsScreen.name)
                            },
                            navController = navController,
                        )
                    }
                    composable(route = MainScreens.PrayerDetailsScreen.name) {
                        PrayerDetailsScreen(
                            prayer = currentPrayer,
                            config = defConfig,
                            prayers = prayers,
                            animatedContentScope = this,
                            sharedTransitionScope = sharedTransitionScope,
                            goBack = { navController.popBackStack() }
                        )
                    }
                    composable(route = MainScreens.SettingsScreen.name) {
                        SettingsScreen(
                            title = stringResource(Res.string.settings_screen_title),
                            animatedContentScope = this,
                            sharedTransitionScope = sharedTransitionScope,
                            config = defConfig,
                            goBack = { navController.popBackStack() }
                        )
                    }
                    composable(route = MainScreens.AboutScreen.name) {
                        AboutScreen(
                            title = stringResource(Res.string.about_screen_title),
                            content = aboutContent,
                            sharedTransitionScope = sharedTransitionScope,
                            goBack = { navController.popBackStack() }
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