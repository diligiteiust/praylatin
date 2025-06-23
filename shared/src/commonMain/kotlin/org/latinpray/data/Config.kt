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
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.latinpray.createSettings

@Serializable
data class Config (
    var uiLang: String,
    var prayerLang: String,
    var secondLang: String,
    var preferTranslation: Boolean,
    var grouping: Boolean,
    var fontScale: Float = 1.0f,
    var donation: String? = null,
    var sharedPrefs: Boolean = false,
    var showNumbers: Boolean = false
) {

    @Transient
    val allPrayerLangs: MutableMap<String, String> = mutableMapOf()
    @Transient
    val allUIlangs: MutableMap<String, String> =
        mutableMapOf("en" to "English", "la" to "Latinae", "pl" to "Polski", "es" to "Español")
    @Transient
    val substitutions = mutableMapOf(
        "patrons" to "my saint patrons..."
    )
    @Transient
    val dailyPrayers: MutableList<String> = mutableListOf()
    @Transient
    val favorites: MutableList<String> = mutableListOf()

    @Transient
    private val UILANF_PROP_KEY = stringPreferencesKey("uiLang")
    @Transient
    private val PRAYERLANG_PROP_KEY = stringPreferencesKey("prayerLang")
    @Transient
    private val SECONDLANG_PROP_KEY = stringPreferencesKey("secondLang")
    @Transient
    private val PREFER_TRANSLATION_PROP_KEY = booleanPreferencesKey("preferTranslation")
    @Transient
    private val GROUPING_PROP_KEY = booleanPreferencesKey("grouping")
    @Transient
    private val FONT_SCALE_PROP_KEY = floatPreferencesKey("fontScale")
    @Transient
    private val DONATION_PROP_KEY = stringPreferencesKey("donation")
    @Transient
    private val SUBS_PROP_KEY = stringPreferencesKey("substitutions")
    @Transient
    private val DAILY_PROP_KEY = stringPreferencesKey("dailyPrayers")
    @Transient
    private val FAVORITES_PROP_KEY = stringPreferencesKey("favorites")
    @Transient
    private val SHARED_PREFS_PROP_KEY = stringPreferencesKey("sharedPrefs")
    @Transient
    private val SHARED_INITIALIZED_PROP_KEY = booleanPreferencesKey("initialized")
    @Transient
    private val SHOW_NUMBERS_PROP_KEY = booleanPreferencesKey("showNumbers")

    @Transient
    private val sharedPrefsSet: MutableSet<String> = mutableSetOf(
        SUBS_PROP_KEY.name,
        DAILY_PROP_KEY.name,
        FAVORITES_PROP_KEY.name,
        PRAYERLANG_PROP_KEY.name,
        SECONDLANG_PROP_KEY.name
    )

    @Transient
    val localSettings: Settings = Settings()

    @Transient
    val sharedSettings: ObservableSettings = createSettings()

    @Transient
    var prayersChangedCallback: () -> Unit = {}
    //var dataStore: DataStore<Preferences>? = null

    //suspend fun loadConfigProps(ds: DataStore<Preferences>) {
    fun loadConfigProps() {
        sharedPrefs = getSharedPrefs()
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
        showNumbers = getShowNumbers()
        loadSubstitutions()
        loadDailyPrayers()
        loadFavorites()
    }

    private fun getPref(key: String, def: String): String {
        println("Getting pref $key")
        var result: String
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            result = sharedSettings.getString(key, def)
            println("Got pref $key from shared prefs: $result")
            // Make copy of shared data in local settings
            localSettings.putString(key, result)
        } else {
            result = localSettings.getString(key, def)
            println("Got pref $key from local settings: $result")
        }
        return result
    }

    fun externalModification() {
        println("External modification")
        loadConfigProps()
        prayersChangedCallback()
    }

    fun setPrayersChangedCallback(prayersChangeListener: () -> Unit) {
        println("Setting prayers changed callback")
        prayersChangedCallback = prayersChangeListener
        sharedSettings.addStringListener(DAILY_PROP_KEY.name, "true") { externalModification() }
        sharedSettings.addStringListener(SUBS_PROP_KEY.name, "true") { externalModification() }
        sharedSettings.addStringListener(FAVORITES_PROP_KEY.name, "true") { externalModification() }
        sharedSettings.addStringListener(
            PRAYERLANG_PROP_KEY.name,
            "true"
        ) { externalModification() }
        sharedSettings.addStringListener(
            SECONDLANG_PROP_KEY.name,
            "true"
        ) { externalModification() }
    }


    private fun getPref(key: String, def: Boolean): Boolean {
        println("Getting pref $key")
        var result: Boolean
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            result = sharedSettings.getBoolean(key, def)
            println("Got pref $key from shared prefs: $result")
            // Make copy of shared data in local settings
            localSettings.putBoolean(key, result)
        } else {
            println("Got pref $key from local settings")
            result = localSettings.getBoolean(key, def)
        }
        return result
    }

    private fun getPref(key: String, def: Float): Float {
        println("Getting pref $key")
        var result: Float
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            result = sharedSettings.getFloat(key, def)
            println("Got pref $key from shared prefs")
            // Make copy of shared data in local settings
            localSettings.putFloat(key, result)
        } else {
            println("Got pref $key from local settings")
            result = localSettings.getFloat(key, def)
        }
        return result
    }

    private fun getFromSharedPrefs(k: String): String {
        var result: String
        if (sharedPrefs) {
            result = sharedSettings.getString(k, "")
            // Make copy in local storage
            localSettings.putString(k, result)
        } else {
            result = localSettings.getString(k, "")
        }
        return result
    }

    private fun setPref(key: String, def: String) {
        println("Setting pref $key")
        println("Setting pref $key in local settings")
        localSettings.putString(key, def)
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            println("Setting pref $key in shared prefs")
            sharedSettings.putString(key, def)
        }
    }

    private fun setPref(key: String, def: Boolean) {
        println("Setting pref $key")
        println("Setting pref $key in local settings")
        localSettings.putBoolean(key, def)
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            println("Setting pref $key in shared prefs")
            sharedSettings.putBoolean(key, def)
        }
    }

    private fun setPref(key: String, def: Float) {
        println("Setting pref $key")
        println("Setting pref $key in local settings")
        localSettings.putFloat(key, def)
        if (sharedPrefs && sharedPrefsSet.contains(key)) {
            println("Setting pref $key in shared prefs")
            sharedSettings.putFloat(key, def)
        }
    }

    private fun putToSharedPrefs(key: String, def: String) {
        localSettings.putString(key, def)
        if (sharedPrefs) sharedSettings.putString(key, def)
    }

    private fun loadSubstitutions() {
        val subs = getPref(SUBS_PROP_KEY.name, "")
        subs.split(',').forEach { k ->
            if (k.isEmpty()) return@forEach
            val v = getFromSharedPrefs(k)
            substitutions[k] = v
        }
    }

    private fun loadDailyPrayers() {
        val daily = getPref(DAILY_PROP_KEY.name, "")
        daily.split(',').forEach {
            if (it.isEmpty() || dailyPrayers.contains(it)) return@forEach
            dailyPrayers.add(it)
        }
    }

    private fun loadFavorites() {
        val favs = getPref(FAVORITES_PROP_KEY.name, "")
        favs.split(',').forEach {
            if (it.isEmpty() || favorites.contains(it)) return@forEach
            favorites.add(it)
        }
    }

    private fun getDonation(): String? {
        val result = getPref(DONATION_PROP_KEY.name, "")
        return result.ifEmpty { null }
    }

    private fun getSharedPrefs(): Boolean =
        getPref(SHARED_PREFS_PROP_KEY.name, sharedPrefs)

    private fun getShowNumbers(): Boolean =
        getPref(SHOW_NUMBERS_PROP_KEY.name, showNumbers)

    private fun getPrayerLang(): String =
        getPref(PRAYERLANG_PROP_KEY.name, prayerLang)

    private fun getUILang(): String =
        getPref(UILANF_PROP_KEY.name, uiLang)

    private fun getSecondLang(): String =
        getPref(SECONDLANG_PROP_KEY.name, secondLang)

    private fun getPreferTranslation(): Boolean =
        getPref(PREFER_TRANSLATION_PROP_KEY.name, preferTranslation)

    private fun getGrouping(): Boolean =
        getPref(GROUPING_PROP_KEY.name, grouping)

    private fun getFontScale(): Float =
        getPref(FONT_SCALE_PROP_KEY.name, fontScale)

    suspend fun saveConfig(
        uiLang: String = this.uiLang,
        prayerLang: String = this.prayerLang,
        secondLang: String = this.secondLang
    ) {
        saveSharedPrefs(sharedPrefs)
        saveUILang(uiLang)
        savePrayerLang(prayerLang)
        saveSecondLang(secondLang)
        savePreferTranslation(preferTranslation)
        saveGrouping(grouping)
        saveFontScale(fontScale)
        saveDonation(donation)
        saveShowNumbers(showNumbers)
        saveSubstitutions()
        saveDailyPrayers()
        saveFavorites()
    }

    fun saveDonation(donation: String?) {
        this.donation = donation
        setPref(DONATION_PROP_KEY.name, donation ?: "")
    }

    suspend fun saveSharedPrefs(pref: Boolean) {
        val copyRequired = (!sharedPrefs && pref)
        sharedPrefs = pref
        setPref(SHARED_PREFS_PROP_KEY.name, sharedPrefs)
        if (copyRequired) {
            if (sharedSettings.getBoolean(SHARED_INITIALIZED_PROP_KEY.name, false)) {
                println("SHARED_INITIALIZED_PROP_KEY already set")
            } else {
                println("SHARED_INITIALIZED_PROP_KEY not set")
                copySharedPrefs()
            }
            sharedSettings.putBoolean(SHARED_INITIALIZED_PROP_KEY.name, true)
        }
    }

    suspend fun resetSharedPrefs() {
        saveSharedPrefs(false)
        sharedSettings.clear()
    }

    suspend fun copySharedPrefs() {
        println("Copying shared prefs")
        saveConfig()
    }

    fun saveUILang(lang: String) {
        uiLang = lang
        setPref(UILANF_PROP_KEY.name, uiLang)
    }

    fun savePrayerLang(lang: String) {
        prayerLang = lang
        setPref(PRAYERLANG_PROP_KEY.name, prayerLang)
    }

    fun saveSecondLang(lang: String) {
        secondLang = lang
        setPref(SECONDLANG_PROP_KEY.name, secondLang)
    }

    fun savePreferTranslation(pref: Boolean) {
        preferTranslation = pref
        setPref(PREFER_TRANSLATION_PROP_KEY.name, preferTranslation)
    }

    fun saveGrouping(pref: Boolean) {
        grouping = pref
        setPref(GROUPING_PROP_KEY.name, grouping)
    }

    fun saveShowNumbers(pref: Boolean) {
        showNumbers = pref
        setPref(SHOW_NUMBERS_PROP_KEY.name, showNumbers)
    }

    fun saveFontScale(scale: Float) {
        fontScale = scale
        setPref(FONT_SCALE_PROP_KEY.name, fontScale)
    }

    fun saveSubstitutions() {
        var subst = ""
        substitutions.forEach { (k, v) ->
            subst += "$k,"
            putToSharedPrefs(k, v)
        }
        setPref(SUBS_PROP_KEY.name, subst)
    }

    fun saveDailyPrayers() {
        var daily = ""
        dailyPrayers.forEach {
            daily += "$it,"
        }
        setPref(DAILY_PROP_KEY.name, daily)
    }

    fun addDailyPrayer(prayer: String) {
        if (dailyPrayers.contains(prayer)) return
        dailyPrayers.add(prayer)
        println("Saving daily prayer $prayer")
        println("Daily prayers: $dailyPrayers")
        saveDailyPrayers()
    }

    fun removeDailyPrayer(prayer: String) {
        dailyPrayers.remove(prayer)
        println("Removing daily prayer $prayer")
        println("Daily prayers: $dailyPrayers")
        saveDailyPrayers()
    }

    fun saveFavorites() {
        var favs = ""
        favorites.forEach {
            favs += "$it,"
        }
        setPref(FAVORITES_PROP_KEY.name, favs)
    }

    fun addFavorite(prayer: String) {
        if (favorites.contains(prayer)) return
        favorites.add(prayer)
        saveFavorites()
    }

    fun removeFavorite(prayer: String) {
        favorites.remove(prayer)
        saveFavorites()
    }

    fun addSubstitution(token: String, value: String) {
        substitutions[token] = value
        saveSubstitutions()
    }

}
