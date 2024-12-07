package org.latinpray.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.latinpray.data.Config
import org.latinpray.data.Prayer

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun PrayerDetailsScreen(
    prayer: Prayer,
    goBack: () -> Unit,
    config: Config,
    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val listState = rememberLazyListState()
    val (fraction, setFraction) = remember { mutableStateOf(0.25f) }

    val lang1 = prayer.langs[config.prayerLang]
    val lang2 = prayer.langs[config.secondLang]

    with(sharedTransitionScope) {
        if (sharedTransitionScope.isTransitionActive.not()) {
            setFraction(0f)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(bottom = 30.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars),
                //verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(50.dp)
                    .align(Alignment.CenterStart)
                    .padding(10.dp)
                    .alpha(fraction)
                    //.alpha(alpha = if (fraction <= 0) 1f else 0f)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(50)
                    ).shadow(elevation = 16.dp).padding(5.dp).clickable {
                        goBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = lang1?.title ?: "No title",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if (lang1 != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    itemsIndexed(lang1.lines) { index, line ->
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                                .background(color = MaterialTheme.colorScheme.background)
                        ) {
                            Text(
                                text = line ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Justify,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (lang2?.lines != null
                                && lang2.lines.size > index
                                && lang2.lines[index]?.isNotEmpty() == true
                            ) {
                                val translation = lang2.lines[index]
                                Text(
                                    text = translation ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Justify,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}