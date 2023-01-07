package com.igorapp.deckster.ui.utils

import androidx.navigation.NavController
import com.igorapp.deckster.platform.Destinations

fun NavController.navigateToSearch() {
    navigate(Destinations.Search.name) {
        launchSingleTop = true
    }
}

fun NavController.navigateToGameDetails(id: String) {
    navigate("${Destinations.Details.name.lowercase()}/${id}") {
        launchSingleTop = true
    }
}

fun NavController.navigateToSettings() {
    navigate(Destinations.Settings.name) {
        launchSingleTop = true
    }
}
