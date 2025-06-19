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

@Serializable
data class Config (
    var uiLang: String,
    var prayerLang: String,
    var secondLang: String,
    var preferTranslation: Boolean,
    var grouping: Boolean,
    var fontScale: Float = 1.0f,
    var donation: String? = null
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

    @Transient
    val settings: Settings = createSettings()
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
        loadSubstitutions()
        loadDailyPrayers()
        loadFavorites()
    }

    private suspend fun loadSubstitutions() {
//        val subs = dataStore!!.data.map {
//            it[SUBS_PROP_KEY] ?: ""
//        }.first()
        val subs = settings.getString(SUBS_PROP_KEY.name, "")
        subs.split(',').forEach { k ->
            if (k.isEmpty()) return@forEach
//            val v = dataStore!!.data.map {
//                it[stringPreferencesKey(k)] ?: ""
//            }.first()
            val v = settings.getString(k, "")
            substitutions[k] = v
        }
    }

    private suspend fun loadDailyPrayers() {
//        val daily = dataStore!!.data.map {
//            it[DAILY_PROP_KEY] ?: ""
//        }.first()
        val daily = settings.getString(DAILY_PROP_KEY.name, "")
        daily.split(',').forEach {
            if (it.isEmpty() || dailyPrayers.contains(it)) return@forEach
            dailyPrayers.add(it)
        }
    }

    private suspend fun loadFavorites() {
//        val favs = dataStore!!.data.map {
//            it[FAVORITES_PROP_KEY] ?: ""
//        }.first()
        val favs = settings.getString(FAVORITES_PROP_KEY.name, "")
        favs.split(',').forEach {
            if (it.isEmpty() || favorites.contains(it)) return@forEach
            favorites.add(it)
        }
    }

    private suspend fun getDonation(): String? =
        settings.getStringOrNull(DONATION_PROP_KEY.name)
//        dataStore!!.data.map {
//            it[DONATION_PROP_KEY]
//        }.first()

    private suspend fun getPrayerLang(): String =
        settings.getString(PRAYERLANG_PROP_KEY.name, prayerLang)
//        dataStore!!.data.map {
//            it[PRAYERLANG_PROP_KEY] ?: prayerLang
//        }.first()

    private suspend fun getUILang(): String =
        settings.getString(UILANF_PROP_KEY.name, uiLang)
//        dataStore!!.data.map {
//            it[UILANF_PROP_KEY] ?: uiLang
//        }.first()

    private suspend fun getSecondLang(): String =
        settings.getString(SECONDLANG_PROP_KEY.name, secondLang)
//        dataStore!!.data.map {
//            it[SECONDLANG_PROP_KEY] ?: secondLang
//        }.first()

    private suspend fun getPreferTranslation(): Boolean =
        settings.getBoolean(PREFER_TRANSLATION_PROP_KEY.name, preferTranslation)
//        dataStore!!.data.map {
//            it[PREFER_TRANSLATION_PROP_KEY] ?: preferTranslation
//        }.first()

    private suspend fun getGrouping(): Boolean =
        settings.getBoolean(GROUPING_PROP_KEY.name, grouping)
//        dataStore!!.data.map {
//            it[GROUPING_PROP_KEY] ?: grouping
//        }.first()

    private suspend fun getFontScale(): Float =
        settings.getFloat(FONT_SCALE_PROP_KEY.name, fontScale)
//        dataStore!!.data.map {
//            it[FONT_SCALE_PROP_KEY] ?: fontScale
//        }.first()

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
    }

    suspend fun saveDonation(donation: String?) {
        this.donation = donation
        settings.putString(DONATION_PROP_KEY.name, donation ?: "")
//        dataStore?.edit {
//            it[DONATION_PROP_KEY] = donation ?: ""
//        }
    }

    suspend fun saveUILang(lang: String) {
        uiLang = lang
        settings.putString(UILANF_PROP_KEY.name, uiLang)
//        dataStore?.edit {
//            it[UILANF_PROP_KEY] = uiLang
//        }
    }

    suspend fun savePrayerLang(lang: String) {
        prayerLang = lang
        settings.putString(PRAYERLANG_PROP_KEY.name, prayerLang)
//        dataStore?.edit {
//            it[PRAYERLANG_PROP_KEY] = prayerLang
//            //println("Saved prayer lang $prayerLang")
//        }
    }

    suspend fun saveSecondLang(lang: String) {
//        if (dataStore == null) {
//            println("DataStore is null")
//        }
        //println("Saving second lang $lang")
        secondLang = lang
        settings.putString(SECONDLANG_PROP_KEY.name, secondLang)
//        dataStore?.edit {
//            it[SECONDLANG_PROP_KEY] = secondLang
//            //println("Saved second lang $secondLang")
//        }
    }

    suspend fun savePreferTranslation(pref: Boolean) {
        preferTranslation = pref
        settings.putBoolean(PREFER_TRANSLATION_PROP_KEY.name, preferTranslation)
//        dataStore?.edit {
//            it[PREFER_TRANSLATION_PROP_KEY] = preferTranslation
//        }
    }

    suspend fun saveGrouping(pref: Boolean) {
        grouping = pref
        settings.putBoolean(GROUPING_PROP_KEY.name, grouping)
//        dataStore?.edit {
//            it[GROUPING_PROP_KEY] = grouping
//        }
    }

    suspend fun saveFontScale(scale: Float) {
        fontScale = scale
        settings.putFloat(FONT_SCALE_PROP_KEY.name, fontScale)
//        dataStore?.edit {
//            it[FONT_SCALE_PROP_KEY] = fontScale
//        }
    }

    suspend fun saveSubstitutions() {
        var subst = ""
        substitutions.forEach { (k, v) ->
            subst += "$k,"
            settings.putString(k, v)
//            dataStore?.edit {
//                it[stringPreferencesKey(k)] = v
//            }
        }
        settings.putString(SUBS_PROP_KEY.name, subst)
//        dataStore?.edit {
//            it[SUBS_PROP_KEY] = subst
//        }
    }

    suspend fun saveDailyPrayers() {
        var daily = ""
        dailyPrayers.forEach {
            daily += "$it,"
        }
        settings.putString(DAILY_PROP_KEY.name, daily)
//        dataStore?.edit {
//            it[DAILY_PROP_KEY] = daily
//        }
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
        settings.putString(FAVORITES_PROP_KEY.name, favs)
//        dataStore?.edit {
//            it[FAVORITES_PROP_KEY] = favs
//        }
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
