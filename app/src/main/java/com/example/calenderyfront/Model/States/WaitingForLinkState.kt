package com.example.calenderyfront.Model.States

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class WaitingForLinkState {
    object Iniciado : WaitingForLinkState() //Estado base
    data class Exito(val userInfo: UserInfo) : WaitingForLinkState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : WaitingForLinkState() // Si el Back falla o no hay internet
}