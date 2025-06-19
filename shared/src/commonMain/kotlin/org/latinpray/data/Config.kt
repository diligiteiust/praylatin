/*
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. Look for COPYING file in the top folder.
 *  If not, see http://www.gnu.org/licenses/.
 */

package org.latinpray.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.latinpray.createSettings
import org.latinpray.io.configStoreFileName
import org.latinpray.io.getDataStore

@Serializable
data class Config (
    var uiLang: String,
    var prayerLang: String,
    var secondLang: String,
    var preferTranslation: Boolean,
    var grouping: Boolean,
    var fontScale: Float = 1.0f,
    var donation: String? = null,
    var sharedPrefs: Boolean = false
) {

    @Transient val allPrayerLangs: MutableMap<String, String> = mutableMapOf()
    @Transient val allUIlangs: MutableMap<String, String> =
        mutableMapOf("en" to "English", "la" to "Latinae", "pl" to "Polski", "es" to "Español")
    @Transient val substitutions = mutableMapOf(
        "patrons" to "my saint patrons..."
    )
    @Transient val dailyPrayers: MutableList<String> = mutableListOf()
    @Transient val favorites: MutableList<String> = mutableListOf()

    @Transient private val UILANF_PROP_KEY = stringPreferencesKey("uiLang")
    @Transient private val PRAYERLANG_PROP_KEY = stringPreferencesKey("prayerLang")
    @Transient private val SECONDLANG_PROP_KEY = stringPreferencesKey("secondLang")
    @Transient private val PREFER_TRANSLATION_PROP_KEY = booleanPreferencesKey("preferTranslation")
    @Transient private val GROUPING_PROP_KEY = booleanPreferencesKey("grouping")
    @Transient private val FONT_SCALE_PROP_KEY = floatPreferencesKey("fontScale")
    @Transient private val DONATION_PROP_KEY = stringPreferencesKey("donation")
    @Transient private val SUBS_PROP_KEY = stringPreferencesKey("substitutions")
    @Transient private val DAILY_PROP_KEY = stringPreferencesKey("dailyPrayers")
    @Transient private val FAVORITES_PROP_KEY = stringPreferencesKey("favorites")
    @Transient private val SHARED_PREFS_PROP_KEY = stringPreferencesKey("sharedPrefs")
    @Transient private val SHARED_INITIALIZED_PROP_KEY = booleanPreferencesKey("initialized")
    @Transient private val sharedPrefsSet: MutableSet<String> = mutableSetOf(
        SUBS_PROP_KEY.name,
        DAILY_PROP_KEY.name,
        FAVORITES_PROP_KEY.name,
        PRAYERLANG_PROP_KEY.name,
        SECONDLANG_PROP_KEY.name
    )

    @Transient
    val localSettings: Settings = Settings()
    @Transient
    val sharedSettings: Settings = createSettings()
    //var dataStore: DataStore<Preferences>? = null

    //suspend fun loadConfigProps(ds: DataStore<Preferences>) {
    suspend fun loadConfigProps() {
        //println("Loading config props")
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
        fontScale = getFontScale()
        //println("Loaded font scale $fontScale")
        donation = getDonation()
        sharedPrefs = getSharedPrefs()
        loadSubstitutions()
        loadDailyPrayers()
        loadFavorites()
    }

    private suspend fun getPref(key: String, def: String): String {
        return if (sharedPrefs && sharedPrefsSet.contains(key)) {
            sharedSettings.getString(key, def)
        } else {
            localSettings.getString(key, def)
        }
    }

    private suspend fun getPref(key: String, def: Boolean): Boolean {
        return if (sharedPrefs && sharedPrefsSet.contains(key)) {
            sharedSettings.getBoolean(key, def)
        } else {
            localSettings.getBoolean(key, def)
        }
    }

    private suspend fun getPref(key: String, def: Float): Float {
        return if (sharedPrefs && sharedPrefsSet.contains(key)) {
            sharedSettings.getFloat(key, def)
        } else {
            localSettings.getFloat(key, def)
        }
    }

    private suspend fun setPref(key: String, def: String) {
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            sharedSettings.putString(key, def)
        } else {
            localSettings.putString(key, def)
        }
    }

    private suspend fun setPref(key: String, def: Boolean) {
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            sharedSettings.putBoolean(key, def)
        } else {
            localSettings.putBoolean(key, def)
        }
    }

    private suspend fun setPref(key: String, def: Float) {
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            sharedSettings.putFloat(key, def)
        } else {
            localSettings.putFloat(key, def)
        }
    }


    private suspend fun loadSubstitutions() {
        val subs = getPref(SUBS_PROP_KEY.name, "")
        subs.split(',').forEach { k ->
            if (k.isEmpty()) return@forEach
            val v = if (sharedPrefs) sharedSettings.getString(k, "") else
                localSettings.getString(k, "")
            substitutions[k] = v
        }
    }

    private suspend fun loadDailyPrayers() {
        val daily = getPref(DAILY_PROP_KEY.name, "")
        daily.split(',').forEach {
            if (it.isEmpty() || dailyPrayers.contains(it)) return@forEach
            dailyPrayers.add(it)
        }
    }

    private suspend fun loadFavorites() {
        val favs = getPref(FAVORITES_PROP_KEY.name, "")
        favs.split(',').forEach {
            if (it.isEmpty() || favorites.contains(it)) return@forEach
            favorites.add(it)
        }
    }

    private suspend fun getDonation(): String? {
        val result = getPref(DONATION_PROP_KEY.name, "")
        return result.ifEmpty { null }
    }

    private suspend fun getSharedPrefs(): Boolean =
        getPref(SHARED_PREFS_PROP_KEY.name, sharedPrefs)

    private suspend fun getPrayerLang(): String =
        getPref(PRAYERLANG_PROP_KEY.name, prayerLang)

    private suspend fun getUILang(): String =
        getPref(UILANF_PROP_KEY.name, uiLang)

    private suspend fun getSecondLang(): String =
        getPref(SECONDLANG_PROP_KEY.name, secondLang)

    private suspend fun getPreferTranslation(): Boolean =
        getPref(PREFER_TRANSLATION_PROP_KEY.name, preferTranslation)

    private suspend fun getGrouping(): Boolean =
        getPref(GROUPING_PROP_KEY.name, grouping)

    private suspend fun getFontScale(): Float =
        getPref(FONT_SCALE_PROP_KEY.name, fontScale)

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
        saveFontScale(fontScale)
        saveDonation(donation)
        saveSubstitutions()
        saveDailyPrayers()
        saveFavorites()
        saveSharedPrefs(sharedPrefs)
    }

    suspend fun saveDonation(donation: String?) {
        this.donation = donation
        setPref(DONATION_PROP_KEY.name, donation ?: "")
    }

    suspend fun saveSharedPrefs(pref: Boolean) {
        val copyRequired = (!sharedPrefs && pref)
        sharedPrefs = pref
        setPref(SHARED_PREFS_PROP_KEY.name, sharedPrefs)
        if (copyRequired && sharedSettings.getBoolean(SHARED_INITIALIZED_PROP_KEY.name, false)) {
            copySharedPrefs()
            sharedSettings.putBoolean(SHARED_INITIALIZED_PROP_KEY.name, true)
        }
    }

    suspend fun copySharedPrefs() {
//        SUBS_PROP_KEY.name,
//        DAILY_PROP_KEY.name,
//        FAVORITES_PROP_KEY.name,
//        PRAYERLANG_PROP_KEY.name,
//        SECONDLANG_PROP_KEY.name
        saveSubstitutions()
        saveDailyPrayers()
        saveFavorites()
        savePrayerLang(prayerLang)
        saveSecondLang(secondLang)
    }

    suspend fun saveUILang(lang: String) {
        uiLang = lang
        setPref(UILANF_PROP_KEY.name, uiLang)
    }

    suspend fun savePrayerLang(lang: String) {
        prayerLang = lang
        setPref(PRAYERLANG_PROP_KEY.name, prayerLang)
    }

    suspend fun saveSecondLang(lang: String) {
        secondLang = lang
        setPref(SECONDLANG_PROP_KEY.name, secondLang)
    }

    suspend fun savePreferTranslation(pref: Boolean) {
        preferTranslation = pref
        setPref(PREFER_TRANSLATION_PROP_KEY.name, preferTranslation)
    }

    suspend fun saveGrouping(pref: Boolean) {
        grouping = pref
        setPref(GROUPING_PROP_KEY.name, grouping)
    }

    suspend fun saveFontScale(scale: Float) {
        fontScale = scale
        setPref(FONT_SCALE_PROP_KEY.name, fontScale)
    }

    suspend fun saveSubstitutions() {
        var subst = ""
        substitutions.forEach { (k, v) ->
            subst += "$k,"
            if (sharedPrefs) sharedSettings.putString(k, v) else localSettings.putString(k, v)
        }
        setPref(SUBS_PROP_KEY.name, subst)
    }

    suspend fun saveDailyPrayers() {
        var daily = ""
        dailyPrayers.forEach {
            daily += "$it,"
        }
        setPref(DAILY_PROP_KEY.name, daily)
    }

    suspend fun addDailyPrayer(prayer: String) {
        if (dailyPrayers.contains(prayer)) return
        dailyPrayers.add(prayer)
        println("Saving daily prayer $prayer")
        println("Daily prayers: $dailyPrayers")
        saveDailyPrayers()
    }

    suspend fun removeDailyPrayer(prayer: String) {
        dailyPrayers.remove(prayer)
        println("Removing daily prayer $prayer")
        println("Daily prayers: $dailyPrayers")
        saveDailyPrayers()
    }

    suspend fun saveFavorites() {
        var favs = ""
        favorites.forEach {
            favs += "$it,"
        }
        setPref(FAVORITES_PROP_KEY.name, favs)
    }

    suspend fun addFavorite(prayer: String) {
        if (favorites.contains(prayer)) return
        favorites.add(prayer)
        saveFavorites()
    }

    suspend fun removeFavorite(prayer: String) {
        favorites.remove(prayer)
        saveFavorites()
    }

    suspend fun addSubstitution(token: String, value: String) {
        substitutions[token] = value
        saveSubstitutions()
    }

}
