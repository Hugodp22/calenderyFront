package com.example.calenderyfront.Model.DataObjects

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

data class PublicacionHome(
    val idUsuario: Int, //Para ir a su perfil
    val nombreUsuario: String,
    val fotoUsuario: String,
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadComentarios: Int,
    val cantidadLikes: Int,
)

data class PublicacionProfile(
    val id: Int,
    val fotoPublicacion: String?,
    val mensaje: String?,
    val cantidadLikes: Int = 0,
    val cantidadComentarios: Int = 0,
    val fechaCalendario: LocalDate, //Fecha que seleccionaste para subirlo
    val fechaPublicacion: LocalDateTime, //Fecha real en la que lo subiste
)

data class PostData(
    val idPost: Int,
    val idUsuario: Int,
    val message: String,
    val calendarDate: String
)

data class TimeData(
    val anio : Int,
    val semana: Int,
    val fechaReferencia: LocalDate
)


