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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
    var grouping: Boolean,
    var fontScale: Float = 1.0f,
    var donation: String? = null
) {

    @Transient val allPrayerLangs: MutableMap<String, String> = mutableMapOf()
    @Transient val allUIlangs: MutableMap<String, String> =
        mutableMapOf("en" to "English", "la" to "Latinae", "pl" to "Polski", "es" to "Español")

    @Transient private val UILANF_PROP_KEY = stringPreferencesKey("uiLang")
    @Transient private val PRAYERLANG_PROP_KEY = stringPreferencesKey("prayerLang")
    @Transient private val SECONDLANG_PROP_KEY = stringPreferencesKey("secondLang")
    @Transient private val PREFER_TRANSLATION_PROP_KEY = booleanPreferencesKey("preferTranslation")
    @Transient private val GROUPING_PROP_KEY = booleanPreferencesKey("grouping")
    @Transient private val FONT_SCALE_PROP_KEY = floatPreferencesKey("fontScale")
    @Transient private val DONATION_PROP_KEY = stringPreferencesKey("donation")

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
        fontScale = getFontScale()
        //println("Loaded font scale $fontScale")
        donation = getDonation()
    }

    private suspend fun getDonation(): String? =
        dataStore!!.data.map {
            it[DONATION_PROP_KEY]
        }.first()

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

    private suspend fun getFontScale(): Float =
        dataStore!!.data.map {
            it[FONT_SCALE_PROP_KEY] ?: fontScale
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
        saveFontScale(fontScale)
        saveDonation(donation)
    }

    suspend fun saveDonation(donation: String?) {
        this.donation = donation
        dataStore?.edit {
            it[DONATION_PROP_KEY] = donation ?: ""
        }
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

    suspend fun saveFontScale(scale: Float) {
        fontScale = scale
        dataStore?.edit {
            it[FONT_SCALE_PROP_KEY] = fontScale
        }
    }

}
