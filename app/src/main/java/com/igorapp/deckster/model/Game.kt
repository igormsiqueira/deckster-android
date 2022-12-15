package com.igorapp.deckster.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey val id: String,
    val game: String,
    val status: String,
    val input: String,
    val runtime: String,
    var isBookmarked: Boolean = false
)
