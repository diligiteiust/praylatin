package org.latinpray.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.latinpray.shared.Res
import org.latinpray.shared.app_icon

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppIcon() {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(Res.drawable.app_icon),
        contentDescription = "App icon"
    )

}