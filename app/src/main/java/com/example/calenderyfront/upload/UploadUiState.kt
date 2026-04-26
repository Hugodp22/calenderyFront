package com.example.calenderyfront.upload

import com.example.calenderyfront.Model.DataObjects.UserInfo

data class UploadUiState (
    val userInfo: UserInfo,
    val fotoSubir: String,
    val mensaje: String,
)