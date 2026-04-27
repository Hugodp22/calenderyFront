package com.example.calenderyfront.settings

import com.example.calenderyfront.Model.dataObjects.UserInfo

sealed class SettingsState {
    object Iniciado : SettingsState() //Estado base
    object Cargando : SettingsState() // Mientras esperamos al Back
    data class Exito(val userInfo: UserInfo) : SettingsState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : SettingsState() // Si el Back falla o no hay internet
}