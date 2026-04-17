package com.example.calenderyfront.Model.DataObjects

data class PublicacionHome(
    val idUsuario: Int, //Para ir a su perfil
    val nombreUsuario: String,
    val fotoUsuario: String,
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadComentarios: Int,
    val cantidadLikes: Int,
)
