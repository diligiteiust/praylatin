package org.latinpray.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.unit.dp
import org.latinpray.getPlatform

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AboutScreen(
    title: String,
    goBack: () -> Unit,
//    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val (fraction, setFraction) = remember { mutableStateOf(0.25f) }
//    val scope = rememberCoroutineScope()

    with(sharedTransitionScope) {
        if (sharedTransitionScope.isTransitionActive.not()) {
            setFraction(0f)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
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
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(50)
                    ).shadow(elevation = 16.dp).padding(5.dp).clickable {
                        goBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
            Box(
                modifier = Modifier.width(196.dp).padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.TopCenter
            ) {
                AppIcon()
            }
//            Box(
//                modifier = Modifier.padding(16.dp)
//                    .align(Alignment.CenterHorizontally),
//            ) {
                Text(
                    text = getPlatform().name,
                    style = MaterialTheme.typography.titleLarge,
                )
//            }
        }
    }
}

