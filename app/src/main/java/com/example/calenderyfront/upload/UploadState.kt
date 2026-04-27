package com.example.calenderyfront.upload

import com.example.calenderyfront.Model.dataObjects.UserInfo

sealed class UploadState {
    object Iniciado : UploadState() //Estado base
    object Cargando : UploadState() // Mientras esperamos al Back
    data class Exito(val userInfo: UserInfo, val postId: Int, val photoUrl: String) : UploadState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : UploadState() // Si el Back falla o no hay internet
}