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

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.todayIn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.latinpray.data.bible.books_abbrev_rev
import org.latinpray.data.bible.books_files
import org.latinpray.io.loadChapter
import org.latinpray.loc.Language

val books = listOf(
    "Rdz",
    "Wj",
    "Kpł",
    "Lb",
    "Pwt",
    "Joz",
    "Sdz",
    "Rt",
    "1Sm",
    "2Sm",
    "1Krl",
    "2Krl",
    "1Krn",
    "2Krn",
    "Ezd",
    "Ne",
    "Tb",
    "Jdt",
    "Es",
    "1Mch",
    "2Mch",
    "Hi",
    "Ps",
    "Prz",
    "Koh",
    "Pnp",
    "Mdr",
    "Syr",
    "Iz",
    "Jr",
    "Lm",
    "Ba",
    "Ez",
    "Dn",
    "Oz",
    "Jo",
    "Am",
    "Ab",
    "Jon",
    "Mi",
    "Na",
    "Ha",
    "So",
    "Ag",
    "Za",
    "Ml",
    "Mt",
    "Mk",
    "Łk",
    "J",
    "Dz",
    "Rz",
    "1Kor",
    "2Kor",
    "Ga",
    "Ef",
    "Flp",
    "Kol",
    "1Tes",
    "2Tes",
    "1Tm",
    "2Tm",
    "Tt",
    "Fil",
    "Hbr",
    "Jk",
    "1P",
    "2P",
    "1J",
    "2J",
    "3J",
    "Jd",
    "Ap"
)

data class BookRef(val book: String, val chapter: Int, val verse: Int) {
    constructor(ref: String) :
            this(
                parseRef(ref, REF_PART.BOOK),
                parseRef(ref, REF_PART.CHAPTER).toInt(),
                parseRef(ref, REF_PART.VERSE).toInt()
            )

    override fun toString(): String {
        return "$book $chapter, $verse"
    }
}

@Serializable
sealed class Reading {
    @SerialName("dayreading")
    @Serializable
    data class Dayreading(val day: String, val start: String, val end: String) : Reading()
}

@Serializable
data class Bible(
    val title: String,
    val subtitle: String,
    val lang: String,
    val language: String,
    val source: String,
    val transcription: String,
    val bible: String,
    val books: MutableList<String>
)

@Serializable
data class Book(
    val bible: String,
    val lang: String,
    val language: String,
    val book: String,
    val title: String,
    val abbrev: String,
    val chapters: MutableList<String>
)

val Map<String, String>.toStringList: List<String>
    get() {
        return this.map { (key, value) ->
            "__${key}__ $value"
        }
    }

@Serializable
data class Chapter(
    override val lang: String,
    override val language: String,
    val bible: String,
    val book: String,
    val chapter: String,
    val verses: MutableMap<String, String>,
    override val links: List<Link>? = null,
    override val notes: String? = null,
    override val tags: Set<String>? = null
) : BssicContent {

    override val title: String
        get() {
            return "$book $chapter"
        }

    override val lines: List<String>
        get() {
            return verses.toStringList
        }
}

data class BibleBasicContent(
    override val title: String,
    override val lang: String,
    override val language: String,
    override val lines: MutableList<String>,
    override val links: List<Link>?,
    override val notes: String?,
    override val tags: Set<String>? = null
) : BssicContent

data class BibleContent(
    override val id: Int,
    override val name: String,
    override val langs: MutableMap<String, BssicContent>
) : Content()

enum class REF_PART {
    BOOK,
    CHAPTER,
    VERSE
}

fun parseRef(ref: String, part: REF_PART): String {
    val split = ref.split(",")
    val verse = split[1].trim()
    val firstpart = split[0].split(" ")
    val chapter = firstpart[firstpart.size - 1].trim()
    var book = firstpart[0].trim()
    if (firstpart.size > 2) {
        book += " " + firstpart[firstpart.size - 2].trim()
    }
    return when (part) {
        REF_PART.BOOK -> book
        REF_PART.CHAPTER -> chapter
        REF_PART.VERSE -> verse
    }
}

@Serializable
data class ReadingPlan(
    val name: String,
    val description: String,
    val version: String,
    val source: String,
    val plan: List<Reading>
) {
    @OptIn(FormatStringsInDatetimeFormats::class)
    @Transient
    val readingDateFormat = LocalDate.Format {
        byUnicodePattern("dd/MM")
    }

    @Transient
    val planMap = mutableMapOf<String, Reading>()

    @Transient
    var todaysReading: Reading.Dayreading? = null

    @Transient
    var todaysContent: Content? = null

    init {
        plan.forEach { reading ->
            when (reading) {
                is Reading.Dayreading -> {
                    planMap[reading.day] = reading
                }
            }
        }
    }

    fun bibleForToday(config: Config): Content? {
        val today = readingDateFormat.format(Clock.System.todayIn(TimeZone.currentSystemDefault()))
        println("Looking for reading for today: $today")
        if (todaysReading == null || todaysReading!!.day != today) {
            todaysContent = null
            planMap[today]?.let {
                todaysReading = it as Reading.Dayreading?
                println("Found reading for today: $it")
            }
            if (todaysReading != null) {
                val startRef = BookRef(todaysReading!!.start)
                println("Reading for today starts: $startRef")
                val endRef = BookRef(todaysReading!!.end)
                println("Reading for today ends: $endRef")
                val bibleContent = BibleContent(
                    1,
                    "dailybible", mutableMapOf()
                )
                val bibleBasicContent = BibleBasicContent(
                    title = "${startRef} - ${endRef}",
                    lang = Language.Polish.isoFormat,
                    language = Language.Polish.name,
                    lines = mutableListOf(),
                    links = null,
                    notes = null
                )
                var moreContent = true
                val bible = "wujek_b"
                var book = books_abbrev_rev[startRef.book]
                var startIndex = startRef.verse - 1
                val endBook = books_abbrev_rev[endRef.book]
                var endIndex = 0
                var chapterNo = startRef.chapter
                val bookfile = books_files[book]
                while (moreContent && bookfile != null) {
                    println("Loading chapter: $book $chapterNo")
                    val chapterCont =
                        loadChapter(bibleBasicContent.lang, bible, bookfile, chapterNo)
                    endIndex = chapterCont.verses.size
                    if (chapterNo == endRef.chapter) {
                        endIndex = endRef.verse
                    }
                    bibleBasicContent.lines.add("### ${chapterCont.title}")
                    bibleBasicContent.lines.addAll(
                        chapterCont.lines.subList(startIndex, endIndex)
                    )

                    if (book == endBook && chapterNo == endRef.chapter) {
                        moreContent = false
                    }
                    chapterNo++
                    startIndex = 0
                }
                bibleContent.langs[bibleBasicContent.lang] = bibleBasicContent
                bibleContent.nums = config.loadContentNums(bibleContent)
                todaysContent = bibleContent
            }
        } else {
            println("Reading for today already found: $todaysReading")
        }
        return todaysContent
    }

}
