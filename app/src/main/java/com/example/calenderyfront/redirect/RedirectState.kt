package com.example.calenderyfront.redirect

import com.example.calenderyfront.Model.dataObjects.UserInfo

sealed class RedirectState {
    object Cargando : RedirectState() // Mientras esperamos al Back
    data class Exito(val userInfo: UserInfo) : RedirectState() //Lo que obtenemos si tenemos exito
    object NoLogin : RedirectState()
    data class NoValidate(val userInfo: UserInfo): RedirectState()
    data class Error(val mensaje: Int): RedirectState()
}