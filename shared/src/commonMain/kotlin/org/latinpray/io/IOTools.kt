
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
import com.ucasoft.kcron.Cron
import com.ucasoft.kcron.core.builders.DelicateIterableApi
import com.ucasoft.kcron.core.common.WeekDays
import kotlinx.serialization.decodeFromString
import okio.buffer
import org.latinpray.data.BasicPrayer
import org.latinpray.data.Chapter
import org.latinpray.data.Config
import org.latinpray.data.Prayer
import org.latinpray.data.ReadingPlan
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

fun readBibleReadingPlan(assetsFile: String, config: Config): ReadingPlan {
    val yamlContent = defaultAssetFileProvider.get(assetsFile).buffer().readUtf8()
    val yaml = Yaml(configuration = Yaml.default.configuration.copy(
        strictMode = false,
        polymorphismStyle = PolymorphismStyle.Property
    ))
    val plan = yaml.decodeFromString<ReadingPlan>(yamlContent)
    return plan
}

fun loadChapter(lang: String, bible: String, book: String, chapter: Int): Chapter {
    val yamlContent = defaultAssetFileProvider.get("assets/bible/$lang/$bible/$book/$chapter.yaml").buffer().readUtf8()
    val yaml = Yaml(configuration = Yaml.default.configuration.copy(
        strictMode = false,
        polymorphismStyle = PolymorphismStyle.Property
    ))
    val chapter = yaml.decodeFromString<Chapter>(yamlContent)
    return chapter
}

fun readFileFromAssets(assetsFile: String): String {
    return defaultAssetFileProvider.get(assetsFile).buffer().readUtf8()
}

fun readConfigFromAssets(assetsFile: String): Config {
    return Yaml.default.decodeFromString<Config>(readFileFromAssets(assetsFile))
}

@OptIn(DelicateIterableApi::class)
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
       // println("Loading prayers for $lang")
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
                config.addExternalPrayerModificationListener(prayer)
                //println("Loaded prayer nums: ${prayer.name} - ${prayer.nums}")
                prayers[name] = prayer
            }
            if (lang == basicPrayer.lang) {
                prayer.langs[lang] = basicPrayer
                basicPrayer.dates?.forEach { date ->
                    try {
                        //println("Parsing date $date to prayer ${prayer.name}")
                        var expr = date
                        if (expr.startsWith("daily")) {
                            expr = "* * * * *"
                        }
                        val builder = Cron.parseAndBuild(expr) {
                            it.firstDayOfWeek = WeekDays.Sunday
                        }
                        prayer.dates.add(builder)
//                        println("Added date $expr to prayer ${prayer.name}, ${builder.asIterable().take(3)}")
//                        if (builder.nextRun?.date == Clock.System.todayIn(TimeZone.currentSystemDefault())
//                            && builder.nextRun?.time?.hour == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour) {
//                            println("Running prayer ${prayer.name} TODAY and NOW!")
//                        }
                    } catch (e: Exception) {
                        println("Error adding date $date to prayer ${prayer.name}, ${e.message}")
                    }
                }
            }
        }
    }

    return prayers.values.toMutableList()
}

