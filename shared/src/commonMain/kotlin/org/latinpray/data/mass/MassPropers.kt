package org.latinpray.data.mass

import kotlinx.serialization.Serializable
import okio.buffer
import org.latinpray.data.ReadingRange
import org.latinpray.data.BookRef
import org.latinpray.io.defaultAssetFileProvider
import org.latinpray.io.yamlParser

@Serializable
data class ProperRange(val s: String, val e: String)

@Serializable
data class MassProperEntry(
    val id: String,
    val title: String,
    val rank: Int? = null,
    val lectio: List<ProperRange> = emptyList(),
    val evangelium: List<ProperRange> = emptyList(),
) {
    fun ranges(): List<ReadingRange> =
        (lectio + evangelium).map { ReadingRange(BookRef(it.s), BookRef(it.e)) }
}

@Serializable
data class MassProperFile(val propers: List<MassProperEntry>)

object MassPropers {
    private val byId: Map<String, MassProperEntry> by lazy { load() }

    private fun load(): Map<String, MassProperEntry> {
        return try {
            val text = defaultAssetFileProvider.get("assets/mass/propers.yaml").buffer().readUtf8()
            val file = yamlParser.decodeFromString(MassProperFile.serializer(), text)
            file.propers.associateBy { it.id }
        } catch (e: Exception) {
            println("Mass propers load failed: ${e.message}")
            emptyMap()
        }
    }

    fun get(id: String): MassProperEntry? {
        byId[id]?.let { return it }
        val stem = id.substringBeforeLast(':').substringBeforeLast(':')
        return byId.entries.firstOrNull { it.key.startsWith(stem) }?.value
    }

    fun sundayOf(id: String): MassProperEntry? {
        // tempora:Pent12-1:4:g -> tempora:Pent12-0:2:g
        val m = Regex("""^(tempora:[A-Za-z]+\d+)-[1-6]""").find(id) ?: return null
        val prefix = m.groupValues[1] + "-0"
        return byId.entries.firstOrNull { it.key.startsWith(prefix) }?.value
    }
}
