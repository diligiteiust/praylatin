package org.latinpray.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Config (
    var uiLang: String,
    var prayerLang: String,
    var secondLang: String,
    var preferTranslation: Boolean,
    var grouping: Boolean
) {

    @Transient val allPrayerLangs: MutableMap<String, String> = mutableMapOf()
    @Transient val allUIlangs: MutableMap<String, String> = mutableMapOf("en" to "English", "la" to "Latinae", "pl" to "Polski")

    @Transient private val UILANF_PROP_KEY = stringPreferencesKey("uiLang")
    @Transient private val PRAYERLANG_PROP_KEY = stringPreferencesKey("prayerLang")
    @Transient private val SECONDLANG_PROP_KEY = stringPreferencesKey("secondLang")
    @Transient private val PREFER_TRANSLATION_PROP_KEY = booleanPreferencesKey("preferTranslation")
    @Transient private val GROUPING_PROP_KEY = booleanPreferencesKey("grouping")

    @Transient
    var dataStore: DataStore<Preferences>? = null

    suspend fun loadConfigProps(ds: DataStore<Preferences>) {
        //println("Loading config props")
        dataStore = ds
        if (dataStore == null) {
            println("DataStore is null")
        }
        prayerLang = getPrayerLang()
        //println("Loaded prayer lang $prayerLang")
        uiLang = getUILang()
        //println("Loaded ui lang $uiLang")
        secondLang = getSecondLang()
        //println("Loaded second lang $secondLang")
        preferTranslation = getPreferTranslation()
        //println("Loaded prefer translation $preferTranslation")
        grouping = getGrouping()
        //println("Loaded grouping $grouping")
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

    private suspend fun getPreferTranslation(): Boolean =
        dataStore!!.data.map {
            it[PREFER_TRANSLATION_PROP_KEY] ?: preferTranslation
        }.first()

    private suspend fun getGrouping(): Boolean =
        dataStore!!.data.map {
            it[GROUPING_PROP_KEY] ?: grouping
        }.first()

    suspend fun saveConfig(
        uiLang: String,
        prayerLang: String,
        secondLang: String
    ) {
        saveUILang(uiLang)
        savePrayerLang(prayerLang)
        saveSecondLang(secondLang)
        savePreferTranslation(preferTranslation)
        saveGrouping(grouping)
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
            //println("Saved prayer lang $prayerLang")
        }
    }

    suspend fun saveSecondLang(lang: String) {
        if (dataStore == null) {
            println("DataStore is null")
        }
        //println("Saving second lang $lang")
        secondLang = lang
        dataStore?.edit {
            it[SECONDLANG_PROP_KEY] = secondLang
            //println("Saved second lang $secondLang")
        }
    }

    suspend fun savePreferTranslation(pref: Boolean) {
        preferTranslation = pref
        dataStore?.edit {
            it[PREFER_TRANSLATION_PROP_KEY] = preferTranslation
        }
    }

    suspend fun saveGrouping(pref: Boolean) {
        grouping = pref
        dataStore?.edit {
            it[GROUPING_PROP_KEY] = grouping
        }
    }

}
