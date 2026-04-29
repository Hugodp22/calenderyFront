package com.example.calenderyfront.profile

import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class ProfileUiState(
    val usuario: UserInfo,
    val otherUserId: Int?,
    val mainId: Int,
    val nombreUsuario: String,
    val fotoUsuario: String,
    val descripcion: String?,
    val cantidadSeguidores: Int = 0,
    val cantidadSeguidos: Int = 0,
    val publicaciones: List<PublicacionProfile> = emptyList(),
    val ultimaPagina: Boolean = false
)
