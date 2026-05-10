package com.example.calenderyfront.Model.DataObjects

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Clase para manejar el registro del usuario y mandarle peticion
 * al back para que lo inserte
 */
data class UserRegister(
    val nombre: String,
    val email: String,
    val keypass: String,
)

/**
 * Clase para manejar el inicio de sesion del usuario mediante
 * correo y contraseña
 */
data class UserLogin(
    val email: String,
    val keypass: String
)

data class UserVisualInfo(
    val fotoPerfil: String,
    val nombreUsuario: String
)

/**
 * Clase para manejar los datos configurables del perfil
 */
data class UserSettings(
    val nombre: String,
    val fotoPerfil: String,
    val descripcion: String?
)

/**
 * Clase para datos del perfil del usuario
 */
data class UserProfile(
    val nombre: String,
    val fotoPerfil: String,
    val descripcion: String = "",
    val cantidadSeguidores: Int = 0,
    val cantidadSeguidos: Int = 0,
    val seguidor: Boolean = false,
    val existeChat: Boolean = false
)

data class SelectionUserChatData(
    val idUsuario: Int,
    val nombre: String,
    val fotoPerfil: String,
    val ultimoMensaje: String? = "",
    val mensajeNuevo: Boolean = false
)

data class SelectionUserProfileData(
    val idUsuario: Int,
    val nombre: String,
    val fotoPerfil: String,
)

data class PageSelectionChatUsers<SelectionUserChatData>(
    val content: List<SelectionUserChatData>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)

data class PageSelectionProfileUsers<SelectionUserProfileData>(
    val content: List<SelectionUserProfileData>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)


/**
 * Informacion del usuario para hacer peticiones y saber que partes de la aplicacion
 * crear segun su Rol
 */
@Serializable
data class UserInfo(
    val idUsuario: Int,
    val roles: List<String>,
)

data class UserValidation(
    val userInfo: UserInfo,
    val enable: Boolean
)

data class PublicKeyDto(
    @SerializedName("publicKey") val publicKey: String
)
