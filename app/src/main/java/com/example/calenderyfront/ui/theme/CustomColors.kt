package com.example.calenderyfront.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val topbarBackground: Color,
    val topbarContent: Color,
    val chatTopbarBackground: Color,
    val chatTopbarContent: Color,
    val bottombarBackground: Color,
    val bottombarContent: Color,
    val bottombarNotification: Color,
    val postCard: Color,
    val postImagePlaceholder: Color,
    val postDivider: Color,
    val chatBubbleMine: Color,
    val chatBubbleOther: Color,
    val profileHeader: Color,
    val profileHeaderText: Color,
    val authCard: Color,
    val spinner: Color,
)

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("ERR -- No se cargó CustomColors")
}
