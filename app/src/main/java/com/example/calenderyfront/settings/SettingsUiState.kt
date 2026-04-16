package com.example.calenderyfront.settings

import com.example.calenderyfront.Model.DataObjects.UserInfo

data class SettingsUiState(
    val userInfo: UserInfo,
    val nombre: String,
    val fotoPerfil: String,
    val descripcion: String
)