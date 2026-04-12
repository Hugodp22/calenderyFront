package com.example.calenderyfront.Model.DataObjects

import com.google.gson.annotations.SerializedName

/**
 * Clase para sacar los datos del usuario mediante el id de lq publicacion para asi cargarla a su lado
 * Con su nombre y foto
 */
data class UserPost(
    val id: Int,
    val nombre: String,
    val foto_perfil: String
)

/**
 * Clase para manejar el registro del usuario y mandarle peticion
 * al back para que lo inserte
 */
data class UserRegister(
    val nombre: String,
    val email: String,
    val keypass: String,
    val clavePublica: String = "AranchaPrueba" //Borrar luego, hay que generarla junto a la privada y pasarla
)

/**
 * Clase para manejar el inicio de sesion del usuario mediante
 * correo y contraseña
 */
data class UserLogin(
    val email: String,
    val keypass: String
)

/**
 * Clase para manejar los datos configurables del perfil
 */
data class UserSettings(
    val id: Int,
    val nombre: String,
    val fotoPerfil: String,
    val descripcion: String
)

/**
 * Clase para datos del perfil del usuario
 */
data class UserProfile(
    val id: Int,
    val nombre: String,
    val fotoPerfil: String,
    val descripcion: String = "", //Puede ser nulo, tener en cuenta
    val cantidadSeguidores: Int = 0,
    val cantidadSeguidos: Int = 0,
    //Igual poner la lista de publicaciones aqui. Si no, cargarlas.
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
