package com.example.calenderyfront.Model.States

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class WaitingToSendTokenState {
    object Iniciado : WaitingToSendTokenState()
    object Cargando : WaitingToSendTokenState()
    data class Exito(val userInfo: UserInfo) : WaitingToSendTokenState()
    data class Error(val mensaje: Int) : WaitingToSendTokenState()
}
