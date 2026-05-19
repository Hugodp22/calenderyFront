package com.example.calenderyfront.bottomBar

import androidx.navigation.NavDestination

data class BottomBarState(
    val currentScreen: NavDestination?,
    var newMessage: Boolean = false
)