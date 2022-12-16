package com.igorapp.deckster.ui.utils

import com.igorapp.deckster.model.Game

val Game.getCapsuleUrl: String
    get() = "https://cdn.cloudflare.steamstatic.com/steam/apps/$id/capsule_sm_120.jpg"
val Game.getCapsuleUrl231: String
    get() =
        "https://cdn.cloudflare.steamstatic.com/steam/apps/$id/capsule_231x87.jpg"
val Game.headerCapsuleImageUrl: String
    get() =
        "https://steamcdn-a.akamaihd.net/steam/apps/$id/header.jpg"
val Game.headerCapsule6x3ImageUrl: String
    get() =
        "https://cdn.cloudflare.steamstatic.com/steam/apps/$id/capsule_616x353.jpg"

val Game.headerUpright: String
    get() =
        "https://cdn.cloudflare.steamstatic.com/steam/apps/$id/library_600x900_2x.jpg?t=1666237217"
//    "https://cdn.cloudflare.steamstatic.com/steam/apps/$id/capsule_616x353.jpg"
