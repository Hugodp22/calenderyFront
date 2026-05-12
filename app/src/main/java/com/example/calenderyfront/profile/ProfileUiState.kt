package com.example.calenderyfront.profile

import com.example.calenderyfront.Model.DataObjects.Comment
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class ProfileUiState(
    val usuario: UserInfo,
    val otherUserId: Int?,
    val mainId: Int,
    val chatId: Int? = null,
    val nombreUsuario: String,
    val fotoUsuario: String,
    val descripcion: String?,
    val cantidadSeguidores: Int = 0,
    val cantidadSeguidos: Int = 0,
    val comment: String = "",
    val miNombre: String? = null,
    val miFoto: String? = null,
    val publicaciones: List<PublicacionProfile> = emptyList(),
    val comentarios: List<Comment> = emptyList(),
    val ultimaPaginaPosts: Boolean = false,
    val ultimaPaginaComments: Boolean = false,
    val seguidor: Boolean = false,
    val existeChat: Boolean = false
)
