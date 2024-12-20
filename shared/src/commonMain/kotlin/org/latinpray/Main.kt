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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.latinpray.data.Config
import org.latinpray.data.sampleConfig
import org.latinpray.data.samplePrayers
import org.latinpray.io.getDataStore
import org.latinpray.io.keyValueStorePath
import org.latinpray.io.prayersList
import org.latinpray.io.readConfigFromAssets
import org.latinpray.shared.Res
import org.latinpray.shared.about_screen_title
import org.latinpray.shared.help_screen_title
import org.latinpray.shared.prayers_screen_title
import org.latinpray.shared.settings_screen_title
import org.latinpray.theme.AppTheme
import org.latinpray.ui.AboutScreen
import org.latinpray.ui.HelpScreen
import org.latinpray.ui.MainScreens
import org.latinpray.ui.PrayerDetailsScreen
import org.latinpray.ui.PrayersListScreen
import org.latinpray.ui.SettingsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Main() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

        var defConfig by remember { mutableStateOf(sampleConfig) }
        var prayers by remember { mutableStateOf(samplePrayers.toMutableList()) }
        var currentPrayer = prayers.first()

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
            prayers = prayersList(prayers, defConfig).sortedBy { prayer ->
                prayer.langs[defConfig.prayerLang]?.title
            }.toMutableList()
            println("Loaded ${prayers.size} prayers")
            currentPrayer = prayers.first()
        }

    AppTheme {
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
                            //animatedContentScope = this,
                            sharedTransitionScope = sharedTransitionScope,
                            goBack = { navController.popBackStack() }
                        )
                    }
                    composable(route = MainScreens.HelpScreen.name) {
                        HelpScreen(
                            title = stringResource(Res.string.help_screen_title),
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