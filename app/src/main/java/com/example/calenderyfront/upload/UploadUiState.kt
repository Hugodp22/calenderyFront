package com.example.calenderyfront.upload

import com.example.calenderyfront.Model.dataObjects.UserInfo

data class UploadUiState (
    val userInfo: UserInfo,
    val fotoSubir: String,
    val urlFoto: String,
)