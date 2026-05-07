package com.example.calenderyfront.profile

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class ProfileState {
    object Iniciado : ProfileState() //Estado base
    object Cargando : ProfileState() // Mientras esperamos al Back
    object Siguiendo: ProfileState()
    object PaginaCargada: ProfileState()
    object likeCargando: ProfileState()
    data class Exito(val userInfo: UserInfo) : ProfileState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : ProfileState() // Si el Back falla o no hay internet
}