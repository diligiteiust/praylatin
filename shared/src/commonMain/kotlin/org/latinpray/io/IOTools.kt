package org.latinpray.io

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import okio.buffer
import org.latinpray.data.BasicPrayer
import org.latinpray.data.Config
import org.latinpray.data.Prayer

fun readPrayerFromAssets(assetsFile: String): BasicPrayer {
    val yamlContent = defaultAssetFileProvider.get(assetsFile).buffer().readUtf8()
    val yaml = Yaml(configuration = Yaml.default.configuration.copy(
        strictMode = false,
        polymorphismStyle = PolymorphismStyle.Property
    ))
    return yaml.decodeFromString<BasicPrayer>(yamlContent)
}

fun readConfigFromAssets(assetsFile: String): Config {
    val yamlContent = defaultAssetFileProvider.get(assetsFile).buffer().readUtf8()
    return Yaml.default.decodeFromString<Config>(yamlContent)
}

fun prayersList(initialPrayers: MutableList<Prayer>, config: Config): MutableList<Prayer>  {
    val prayers = emptyMap<String, Prayer>().toMutableMap()
    initialPrayers.forEach { prayer ->
        //println("initial prayer: ${prayer.name}")
        prayers[prayer.name] = prayer
    }

    val langs = listAssetsInDirectory("assets/prayers/")
    langs.forEach { lang ->
        println("Loading prayers for $lang")
        val prs = listAssetsInDirectory("assets/prayers/$lang/")
        var i = 1
        prs.forEach { pr ->
            println("Loading prayer $pr")
            val name = pr.removeSuffix(".yaml")
            val basicPrayer = readPrayerFromAssets("assets/prayers/$lang/$pr")
            println("Loaded prayer ${basicPrayer.title}")
            println("Notes: ${basicPrayer.notes}")
            var prayer = prayers[name]
            if (prayer == null) {
                //println("Creating new prayer $name")
                prayer = Prayer(i++, name, mutableMapOf(basicPrayer.lang to basicPrayer))
                prayers[name] = prayer
            }
            if (lang == basicPrayer.lang) {
                prayer.langs[lang] = basicPrayer
                if (!lang.endsWith("_tr")) {
                    config.allPrayerLangs[basicPrayer.lang] = basicPrayer.language
                }
            }
        }
    }

    return prayers.values.toMutableList()
}

