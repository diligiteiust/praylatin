package org.latinpray.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Link {
    @SerialName("youtube")
    @Serializable
    data class Youtube(val url: String) : Link()
}

@Serializable
data class BasicPrayer(
    val title: String,
    val lang: String,
    val language: String,
    val lines: List<String?>,
    val tags: List<String>? = null,
    val links: List<Link>? = null,
    val notes: String? = null
)

data class Prayer(
    val id: Int,
    val name: String,
    val langs: MutableMap<String, BasicPrayer>
)
