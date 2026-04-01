package com.example.calenderyfront.Model.States

import com.example.calenderyfront.Model.DataObjects.UserProfile

sealed class ProfileSettingsState {
    object Iniciado : ProfileSettingsState() //Estado base
    object Cargando : ProfileSettingsState() // Mientras esperamos al Back
    data class Exito(val usuario: UserProfile) : ProfileSettingsState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : ProfileSettingsState() // Si el Back falla o no hay internet
}