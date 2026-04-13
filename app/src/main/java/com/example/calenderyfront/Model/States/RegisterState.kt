package com.example.calenderyfront.Model.States

sealed class RegisterState {
    object Iniciado : RegisterState() //Estado base
    object Cargando : RegisterState() // Mientras esperamos al Back
    data class Exito(val userId: Int) : RegisterState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : RegisterState() // Si el Back falla o no hay internet
}