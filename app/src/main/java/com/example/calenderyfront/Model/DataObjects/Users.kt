package com.example.calenderyfront.Model.DataObjects

import com.google.gson.annotations.SerializedName

/**
 * Clase para sacar los datos del usuario mediante la publicacion para asi cargarla a su lado
 */
data class UserPost(
    val id: Int,
    val nombre: String,
    val foto_perfil: String
)

/**
 * Clase para manejar el logeo del usuario
 */
data class UserLog(
    val nombre: String,
    val email: String,
    val keypass: String,
    val clavePublica: String = "TrenPrueba" //Borrar luego, hay que generarla junto a la privada
)

/**
 * Clase para datos del perfil del usuario
 */
data class UserProfile(
    val id: Int,
    val nombre: String,
    val fotoPerfil: String,
    val descripcion: String = "",
    val cantidadSeguidores: Int = 0,
    val cantidadSeguidos: Int = 0,
    val publicaciones: List<Publicacion> = emptyList()
)

data class UserSecurity(
    val id: Int,
    val nombre: String,
    val email: String,
    val keypass: String,
    val clave_publica: String
)

/**
 * @SerializedName asegura que, aunque cambies el nombre de la variable en Kotlin,
 * busque la clave exacta que envía el JSON de Java.
 */
data class Usuario(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String
)
