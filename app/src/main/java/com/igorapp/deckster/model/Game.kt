package com.igorapp.deckster.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey val id: String,
    val name: String,
    val status: String,
    val isBookmarked: Boolean = false
)
