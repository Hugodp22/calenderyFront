package com.example.calenderyfront.register

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class RegisterState {
    object Iniciado : RegisterState() //Estado base
    object Cargando : RegisterState() // Mientras esperamos al Back
    data class Exito(val userInfo: UserInfo) : RegisterState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : RegisterState() // Si el Back falla o no hay internet
}