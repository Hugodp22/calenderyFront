package com.example.calenderyfront.Model.DataObjects

import java.time.LocalDate
import java.time.LocalDateTime

data class PublicacionHome(
    val idUsuario: Int, //Para ir a su perfil
    val idPost: Int,
    val nombreUsuario: String,
    val fotoUsuario: String,
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadComentarios: Int,
    val cantidadLikes: Int,
    val like: Boolean = false
)

data class PublicacionProfile(
    val id: Int,
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadLikes: Int = 0,
    val cantidadComentarios: Int = 0,
    val fechaCalendario: String, //Fecha que seleccionaste para subirlo
    val fechaPublicacion: String, //Fecha real en la que lo subiste
    var like: Boolean = false
)

data class PostUIData(
    val postId: Int,
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadLikes: Int,
    val cantidadComentarios: Int
)

data class PostData(
    val idPost: Int,
    val idUsuario: Int,
    val message: String,
    val calendarDate: String
)

data class Comment(
    val idUsuario: Int,
    val idComentario: Int,
    val nombreUsuario: String,
    val fotoUsuario: String,
    val comentario: String,
    val localDateTime: LocalDateTime ? = null
)

data class PageProfilePosts<PublicacionProfile>(
    val content: List<PublicacionProfile>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)

data class PagePostComments<Comment>(
    val content: List<Comment>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)

data class TimeData(
    val anio : Int,
    val semana: Int,
    val fechaReferencia: LocalDate
)


