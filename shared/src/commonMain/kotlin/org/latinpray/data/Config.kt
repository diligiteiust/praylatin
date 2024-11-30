package org.latinpray.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.latinpray.io.getDataStore

@Serializable
data class Config (
    var uiLang: String,
    var prayerLang: String,
    var secondLang: String
) {

    @Transient val allPrayerLangs: MutableMap<String, String> = mutableMapOf()
    @Transient val allUIlangs: MutableMap<String, String> = mutableMapOf("en" to "English", "la" to "Latinae", "pl" to "Polski")

    @Transient private val UILANF_PROP_KEY = stringPreferencesKey("uiLang")
    @Transient private val PRAYERLANG_PROP_KEY = stringPreferencesKey("prayerLang")
    @Transient private val SECONDLANG_PROP_KEY = stringPreferencesKey("secondLang")
    @Transient
    var dataStore: DataStore<Preferences>? = null

    suspend fun loadConfigProps(ds: DataStore<Preferences>) {
        println("Loading config props")
        dataStore = ds
        if (dataStore == null) {
            println("DataStore is null")
        }
        prayerLang = getPrayerLang()
        println("Loaded prayer lang $prayerLang")
        uiLang = getUILang()
        println("Loaded ui lang $uiLang")
        secondLang = getSecondLang()
        println("Loaded second lang $secondLang")
    }

    private suspend fun getPrayerLang(): String =
        dataStore!!.data.map {
            it[PRAYERLANG_PROP_KEY] ?: prayerLang
        }.first()

    private suspend fun getUILang(): String =
        dataStore!!.data.map {
            it[UILANF_PROP_KEY] ?: uiLang
        }.first()

    private suspend fun getSecondLang(): String =
        dataStore!!.data.map {
            it[SECONDLANG_PROP_KEY] ?: secondLang
        }.first()

    suspend fun saveConfig(
        uiLang: String,
        prayerLang: String,
        secondLang: String
    ) {
        saveUILang(uiLang)
        savePrayerLang(prayerLang)
        saveSecondLang(secondLang)
    }

    suspend fun saveUILang(lang: String) {
        uiLang = lang
        dataStore?.edit {
            it[UILANF_PROP_KEY] = uiLang
        }
    }

    suspend fun savePrayerLang(lang: String) {
        prayerLang = lang
        dataStore?.edit {
            it[PRAYERLANG_PROP_KEY] = prayerLang
            println("Saved prayer lang $prayerLang")
        }
    }

    suspend fun saveSecondLang(lang: String) {
        if (dataStore == null) {
            println("DataStore is null")
        }
        println("Saving second lang $lang")
        secondLang = lang
        dataStore?.edit {
            it[SECONDLANG_PROP_KEY] = secondLang
            println("Saved second lang $secondLang")
        }
    }


}

//class ConfigDataStore(
//    private val dataStore: DataStore<Preferences>,
//    var config: Config = sampleConfig
//) {
//
//    private val UILANF_PROP_KEY = stringPreferencesKey("uiLang")
//    private val PRAYERLANG_PROP_KEY = stringPreferencesKey("prayerLang")
//    private val SECONDLANG_PROP_KEY = stringPreferencesKey("secondLang")
//
////    val config: Flow<Config> = dataStore.data.map {
////        Config(
////            it[UILANF_PROP_KEY] ?: sampleConfig.uiLang,
////            it[PRAYERLANG_PROP_KEY] ?: sampleConfig.prayerLang,
////            it[SECONDLANG_PROP_KEY] ?: sampleConfig.secondLang
////        )
////    }
//
//    suspend fun loadConfigProps() {
//        dataStore.data.map {
//            config.uiLang = it[UILANF_PROP_KEY] ?: sampleConfig.uiLang
//            config.prayerLang = it[PRAYERLANG_PROP_KEY] ?: sampleConfig.prayerLang
//            config.secondLang = it[SECONDLANG_PROP_KEY] ?: sampleConfig.secondLang
//        }
//    }
//
//    suspend fun saveConfig(
//        uiLang: String,
//        prayerLang: String,
//        secondLang: String
//    ) {
//        saveUILang(uiLang)
//        savePrayerLang(prayerLang)
//        saveSecondLang(secondLang)
//    }
//
//    suspend fun saveUILang(uiLang: String) {
//        config.uiLang = uiLang
//        dataStore.edit {
//            it[UILANF_PROP_KEY] = uiLang
//        }
//    }
//
//    suspend fun savePrayerLang(prayerLang: String) {
//        config.prayerLang = prayerLang
//        dataStore.edit {
//            it[PRAYERLANG_PROP_KEY] = prayerLang
//        }
//    }
//
//    suspend fun saveSecondLang(secondLang: String) {
//        config.secondLang = secondLang
//        dataStore.edit {
//            it[SECONDLANG_PROP_KEY] = secondLang
//        }
//    }
//
//}