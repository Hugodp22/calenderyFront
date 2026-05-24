package com.example.calenderyfront.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrincipalOscuro, //Fondo
    secondary = SecundarioOscuro,
    tertiary = LetrasOscuro, //Letras
    onPrimary = FondoLeveOscuro,
    onSecondary = BotonPerfilOscuro,
    onTertiary = BarraCargaOscuro, //Barra de carga
    primaryContainer = BotonCargandoSeguirOscuro,
    secondaryContainer = BotonCargandoDejarSeguirOscuro,
    tertiaryContainer = BotonDejarSeguirOscuro,
    onPrimaryFixed = ColorNotificacionOscuro,
    onSecondaryFixed = FondoIconosPublicacionesOscuro,
    onTertiaryFixed = ColorCardsOscuro,
    onPrimaryContainer = TextoLinkOscuro,
    onSecondaryContainer = BarraCargaCardOscuro,
    onTertiaryContainer = BotonGeneralOscuro,
    primaryFixed = BotonUploadOscuro,
)

private val LightColorScheme = lightColorScheme(
    primary = PrincipalClaro,
    secondary = SecundarioClaro,
    tertiary = LetrasClaro,
    onPrimary = FondoLeveClaro,
    onSecondary = BotonPerfilClaro,
    onTertiary = BarraCargaClaro,
    primaryContainer = BotonCargandoSeguirClaro,
    secondaryContainer = BotonCargandoDejarSeguirClaro,
    tertiaryContainer = BotonDejarSeguirClaro,
    onPrimaryFixed = ColorNotificacionClaro,
    onSecondaryFixed = FondoIconosPublicacionesClaro,
    onTertiaryFixed = ColorCardsClaro,
    onPrimaryContainer = TextoLinkClaro,
    onSecondaryContainer = BarraCargaCardClaro,
    onTertiaryContainer = BotonGeneralClaro,
    primaryFixed = BotonUploadClaro,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CalenderyFrontTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}