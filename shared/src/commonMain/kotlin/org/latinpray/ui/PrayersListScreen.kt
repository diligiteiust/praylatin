package org.latinpray.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.latinpray.data.Config
import org.latinpray.data.Prayer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PrayersListScreen(
    title: String,
    prayers: List<Prayer>,
    config: Config,
    onClick: (prayer: Prayer) -> Unit,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val (fraction) = remember { mutableStateOf(0.50f) }
    val expanded : MutableState<Boolean> = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
        .fillMaxSize()
//        .background(color = MaterialTheme.colorScheme.onBackground)
        .padding(bottom = 30.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            Box(modifier = Modifier.size(50.dp)
                .align(Alignment.CenterStart)
                .padding(10.dp)
                .alpha(fraction)
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(50)
                ).shadow(elevation = 16.dp).padding(5.dp).clickable {
                    expanded.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(30.dp)
                )
            }
            MainMenu(
                navController = navController,
                isExpanded = expanded
            )

            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
        LazyColumn {
            items(items = prayers) { item ->
                if (item.langs[config.prayerLang] !=null) {
                    PrayerListItem(
                        prayer = item,
                        config = config,
                        onClick = onClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}

