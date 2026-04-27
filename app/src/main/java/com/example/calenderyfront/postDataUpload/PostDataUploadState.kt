package com.example.calenderyfront.postDataUpload

import com.example.calenderyfront.Model.DataObjects.UserInfo

sealed class PostDataUploadState {
    object Iniciado : PostDataUploadState() //Estado base
    object Cargando : PostDataUploadState() // Mientras esperamos al Back
    data class Exito(val userInfo: UserInfo) : PostDataUploadState() //Lo que obtenemos si tenemos exito
    data class Error(val mensaje: Int) : PostDataUploadState() // Si el Back falla o no hay internet
}