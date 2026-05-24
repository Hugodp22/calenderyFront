package com.example.calenderyfront.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val LightCustom = CustomColors(
    topbarBackground = LightTopbarBackground,
    topbarContent = LightTopbarContent,
    chatTopbarBackground = LightChatTopbarBackground,
    chatTopbarContent = LightChatTopbarContent,
    bottombarBackground = LightBottombarBackground,
    bottombarContent = LightBottombarContent,
    bottombarNotification = LightBottombarNotification,
    postCard = LightPostCard,
    postImagePlaceholder = LightPostImagePlaceholder,
    postDivider = LightPostDivider,
    chatBubbleMine = LightChatBubbleMine,
    chatBubbleOther = LightChatBubbleOther,
    profileHeader = LightProfileHeader,
    profileHeaderText = LightProfileHeaderText,
    authCard = LightAuthCard,
    spinner = LightPrimary,
)

private val DarkCustom = CustomColors(
    topbarBackground = DarkTopbarBackground,
    topbarContent = DarkTopbarContent,
    chatTopbarBackground = DarkChatTopbarBackground,
    chatTopbarContent = DarkChatTopbarContent,
    bottombarBackground = DarkBottombarBackground,
    bottombarContent = DarkBottombarContent,
    bottombarNotification = DarkBottombarNotification,
    postCard = DarkPostCard,
    postImagePlaceholder = DarkPostImagePlaceholder,
    postDivider = DarkPostDivider,
    chatBubbleMine = DarkChatBubbleMine,
    chatBubbleOther = DarkChatBubbleOther,
    profileHeader = DarkProfileHeader,
    profileHeaderText = DarkProfileHeaderText,
    authCard = DarkAuthCard,
    spinner = DarkPrimary,
)

private val LightColorScheme = lightColorScheme(
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
)

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
)

@Composable
fun CalenderyFrontTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val custom = if (darkTheme) DarkCustom else LightCustom

    CompositionLocalProvider(LocalCustomColors provides custom) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
