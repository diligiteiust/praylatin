package org.latinpray.android

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.latinpray.AndroidInjector
import org.latinpray.Main

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjector.init(applicationContext as Application)
        setContent {
            Main()
        }
    }
}

