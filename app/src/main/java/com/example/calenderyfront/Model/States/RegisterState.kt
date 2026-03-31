package com.example.calenderyfront.Model.States

import com.example.calenderyfront.Model.DataObjects.UserProfile

sealed class RegisterState {
    object Iniciado : RegisterState() //Estado base
    object Cargando : RegisterState() // Mientras esperamos al Back
    data class Exito(val usuario: UserProfile) : RegisterState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: String) : RegisterState() // Si el Back falla o no hay internet
}