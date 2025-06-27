
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

package org.latinpray.io

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import okio.buffer
import org.latinpray.data.BasicPrayer
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.loc.getLanguage

val pattern = Regex("\\$[a-zA-Z0-9]+\\b")

fun readPrayerFromAssets(assetsFile: String, config: Config): BasicPrayer {
    val yamlContent = defaultAssetFileProvider.get(assetsFile).buffer().readUtf8()
    //println("Yaml content: $assetsFile")
    val allsubs = pattern.findAll(yamlContent)
    for (sub in allsubs) {
        val token = sub.value.substring(1)
        //println("Found substitution: ${token}")
        if (!config.substitutions.containsKey(token)) {
            config.addSubstitution(token, "")
        }
    }
    val yaml = Yaml(configuration = Yaml.default.configuration.copy(
        strictMode = false,
        polymorphismStyle = PolymorphismStyle.Property
    ))
    val prayer = yaml.decodeFromString<BasicPrayer>(yamlContent)
    for (sub in allsubs) {
        val token = sub.value.substring(1)
        val substitution = config.substitutions[token] ?: ""
        val newpattern = Regex("\\$${token}\\b")
        for ((index, line) in prayer.lines.withIndex()) {
            prayer.lines[index] = line?.replace(newpattern, substitution)
        }
    }
    return prayer
}

fun readFileFromAssets(assetsFile: String): String {
    return defaultAssetFileProvider.get(assetsFile).buffer().readUtf8()
}

fun readConfigFromAssets(assetsFile: String): Config {
    return Yaml.default.decodeFromString<Config>(readFileFromAssets(assetsFile))
}

fun prayersList(initialPrayers: MutableList<Prayer>, config: Config): MutableList<Prayer>  {
    val prayers = emptyMap<String, Prayer>().toMutableMap()
    initialPrayers.forEach { prayer ->
        //println("initial prayer: ${prayer.name}")
        prayers[prayer.name] = prayer
    }

    val langs = listAssetsInDirectory("assets/prayers/")
    langs.forEach { lang ->
        if (!lang.endsWith("_tr")) {
            config.allPrayerLangs[lang] = getLanguage(lang).name
        }
        if (lang != config.prayerLang && lang != config.secondLang
            && lang != config.prayerLang + "_tr" &&  lang != config.secondLang + "_tr") return@forEach
        //println("Loading prayers for $lang")
        val prs = listAssetsInDirectory("assets/prayers/$lang/")
        var i = 1
        prs.forEach { pr ->
            //println("Loading prayer $pr for lang: $lang")
            val name = pr.removeSuffix(".yaml")
            val basicPrayer = readPrayerFromAssets("assets/prayers/$lang/$pr", config)
            //println("Loaded prayer ${basicPrayer.title}")
            //println("Notes: ${basicPrayer.notes}")
            var prayer = prayers[name]
            if (prayer == null) {
                //println("Creating new prayer $name")
                prayer = Prayer(i++, name, mutableMapOf(basicPrayer.lang to basicPrayer))
                prayer.nums = config.loadPrayerNums(prayer)
                prayers[name] = prayer
            }
            if (lang == basicPrayer.lang) {
                prayer.langs[lang] = basicPrayer
            }
        }
    }

    return prayers.values.toMutableList()
}

