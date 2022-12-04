package com.igorapp.deckster.ui.utils

object ImageUrlBuilder {
    fun getCapsuleUrl(appId: String): String =
        "https://cdn.cloudflare.steamstatic.com/steam/apps/${appId}/capsule_sm_120.jpg"
    fun getCapsuleUrl231(appId: String): String =
        "https://cdn.cloudflare.steamstatic.com/steam/apps/${appId}/capsule_231x87.jpg"

    fun getHeaderUrl(appId: String): String =
        "https://steamcdn-a.akamaihd.net/steam/apps/${appId}/header.jpg"

    fun getCapsuleHeaderUrl(appId: String): String =
        "https://cdn.cloudflare.steamstatic.com/steam/apps/${appId}/capsule_616x353.jpg"
}