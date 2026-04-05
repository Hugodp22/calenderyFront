package com.example.calenderyfront.Model.States

import com.example.calenderyfront.Model.DataObjects.UserSettings

sealed class RegisterState {
    object Iniciado : RegisterState() //Estado base
    object Cargando : RegisterState() // Mientras esperamos al Back
    data class Exito(val usuario: UserSettings) : RegisterState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : RegisterState() // Si el Back falla o no hay internet
}