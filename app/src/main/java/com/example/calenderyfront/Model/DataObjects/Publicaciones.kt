package com.example.calenderyfront.Model.DataObjects

data class Publicacion(
    val id: Int,
    val idUsuario: Int, //Para luego buscarlo y usarlo en
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadComentarios: Int,
    val cantidadLikes: Int,
    val fechaPublicacion: String //No se que poner
)