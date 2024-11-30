package org.latinpray.io

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.latinpray.AndroidInjector

// shared/src/androidMain/kotlin/createDataStore.android.kt

//fun createConfigDataStore(context: Context): DataStore<Preferences> = createConfigDataStore(
//    producePath = { context.filesDir.resolve(configStoreFileName).absolutePath }
//)

//fun getDataStore(context: Context): DataStore<Preferences> = getDataStore(
//    producePath = { context.filesDir.resolve(configStoreFileName).absolutePath }
//)

actual fun keyValueStorePath(): String {
    return AndroidInjector.application.applicationContext.filesDir.resolve("datastore/$configStoreFileName").absolutePath
}