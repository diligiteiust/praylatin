package org.latinpray.data

import kotlinx.serialization.Serializable

@Serializable
data class BasicPrayer(
    val title: String,
    val lang: String,
    val language: String,
    val lines: List<String?>
)

data class Prayer(
    val id: Int,
    val name: String,
    val langs: MutableMap<String, BasicPrayer>
)
