package com.example.calenderyfront.home

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class HomeState {
    object Iniciado : HomeState() //Estado base
    object Cargando : HomeState() // Mientras esperamos al Back
    object PostCargados: HomeState()
    data class Exito(val userInfo: UserInfo) : HomeState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : HomeState() // Si el Back falla o no hay internet
}