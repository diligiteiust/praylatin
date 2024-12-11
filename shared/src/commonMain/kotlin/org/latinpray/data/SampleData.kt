package org.latinpray.data

val avemaryjaLa = BasicPrayer(
    "Ave Maryja",
    "la",
    "Latina",
    listOf(
        "Ave Maria, gratia plena,",
        "Dominus tecum.",
        "Benedicta tu in mulieribus,",
        "et benedictus fructus ventris tui, Iesus.",
        "Sancta Maria, Mater Dei,",
        "ora pro nobis peccatoribus,",
        "nunc, et in hora mortis nostrae."
    )
)
val avemaryjaPl = BasicPrayer(
    "Zdrowaś Maryjo",
    "pl",
    "Polski",
    listOf(
        "Zdrowaś Maryjo, łaski pełna",
        "Pan z Tobą",
        "Błogosławionaś Ty między niewiastami",
        "i błogosławiony owoc żywota Twojego, Jezus.",
        "Święta Maryjo, Matko Boża",
        "Módl się za nami grzesznikami,",
        "Teraz, i w godzinie śmierci naszej."
    )
)

val paternosterLa = BasicPrayer(
        "Pater Noster",
    "la",
    "Latine",
    listOf(
        "Pater Noster,",
        "qui es in caelis,",
        "sanctificetur nomen tuum.",
        "Adveniat regnum tuum.",
        "Fiat voluntas tua,",
        "sicut in caelo et in terra.",
        "Panem nostrum quotidianum da nobis hodie,",
        "et dimitte nobis debita nostra",
        "sicut et nos dimittimus debitoribus nostris.",
        "Et ne nos inducas in tentationem,",
        "sed libera nos a malo."
    )
)
val paternosterPl = BasicPrayer(
    "Ojcze nasz",
    "pl",
    "Polski",
    listOf(
        "Ojcze nasz,",
        "któryś jest w niebie,",
        "święć się imię Twoje,",
        "przyjdź królestwo Twoje,",
        "bądź wola Twoja,",
        "jako w niebie, tak i na ziemi.",
        "Chleba naszego powszedniego daj nam dzisiaj,",
        "i odpuść nam nasze winy,",
        "jako i my odpuszczamy naszym winowajcom.",
        "I nie wódź nas na pokuszenie,",
        "ale nas zbaw ode złego."
    )
)

val avemaria = Prayer(
    1,
    "avemaria",
    mutableMapOf(
        "la" to avemaryjaLa,
        "pl" to avemaryjaPl
    )
)

val paternoster = Prayer(
    2,
    "paternoster",
    mutableMapOf(
        "la" to paternosterLa,
        "pl" to paternosterPl
    )
)

val samplePrayers = listOf(avemaria, paternoster)

var sampleConfig = Config(
    "en",
    "la",
    "en",
    true,
    true)
//var defConfig : Config? = sampleConfig