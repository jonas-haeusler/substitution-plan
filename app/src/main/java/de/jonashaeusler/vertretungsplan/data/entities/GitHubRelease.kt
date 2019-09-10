package de.jonashaeusler.vertretungsplan.data.entities

import com.squareup.moshi.Json

data class GitHubRelease(
        @field:Json(name = "tag_name") val version: String,
        @field:Json(name = "name") val name: String,
        @field:Json(name = "assets") private val assets: List<GitHubAssets>
) {

    val downloadLink: String
        get() = assets[0].downloadLink
}

data class GitHubAssets(
        @field:Json(name = "browser_download_url") val downloadLink: String
)
