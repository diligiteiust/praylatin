package org.latinpray.io

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import okio.Path.Companion.toPath

private lateinit var dataStore: DataStore<Preferences>

@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
@OptIn(InternalCoroutinesApi::class)
fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }

internal const val configStoreFileName = "latinpray_config.preferences_pb"

expect fun keyValueStorePath(): String

///**
// * Gets the singleton DataStore instance, creating it if necessary.
// */
//fun createConfigDataStore(producePath: () -> String): DataStore<Preferences> =
//    PreferenceDataStoreFactory.createWithPath(
//        produceFile = { producePath().toPath() }
//    )
//
//internal const val configStoreFileName = "latinpray_config.preferences_pb"
//
//val configDataStore: DataStore<Preferences> by lazy {
//    createConfigDataStore({ configStoreFileName })
//}
