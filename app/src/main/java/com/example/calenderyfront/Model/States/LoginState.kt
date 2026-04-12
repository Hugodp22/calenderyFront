package com.example.calenderyfront.Model.States

sealed class LoginState {
    object Iniciado : LoginState() //Estado base
    object Cargando : LoginState() // Mientras esperamos al Back
    data class Exito(val userId: Int) : LoginState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : LoginState() // Si el Back falla o no hay internet
}