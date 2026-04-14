package com.example.calenderyfront.Model.States

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class LoginState {
    object Iniciado : LoginState() //Estado base
    object Cargando : LoginState() // Mientras esperamos al Back
    data class Exito(val userInfo: UserInfo) : LoginState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : LoginState() // Si el Back falla o no hay internet
}