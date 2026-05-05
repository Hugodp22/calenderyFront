package com.example.calenderyfront.selection

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class SelectionState {
    object Iniciado : SelectionState() //Estado base
    object Cargando : SelectionState() // Mientras esperamos al Back
    object PaginaCargada: SelectionState()
    data class Exito(val userInfo: UserInfo, val otherUserId: Int) : SelectionState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : SelectionState() // Si el Back falla o no hay internet
}