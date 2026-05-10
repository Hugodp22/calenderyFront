package com.example.calenderyfront.Model.DataObjects

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
object Redirect

@Serializable
object Register

@Serializable
object Login

@Serializable
data class Settings(val userInfo: UserInfo)

@Serializable
data class VerifyLink(val userInfo: UserInfo)

@Serializable
data class Home(val userInfo: UserInfo)

@Serializable
data class Profile(val userInfo: UserInfo, val otherUserID: Int? = null)

@Serializable
data class Upload(val userInfo: UserInfo)

@Serializable
data class PostDataUpload(val userInfo: UserInfo, val postId: Int, val photoPath: String, val photoUrl: String)

@Serializable
data class Selection(val userInfo: UserInfo, val chatOption : Boolean)

@Serializable
data class Chat(val userInfo: UserInfo, val otherUserId: Int, val otherUserName: String, val otherUserPhoto: String)


/**
 * NavType personalizado para que sea capaz de utilizar el objeto UserInfo para pasarlo entre
 * diferentes rutas de composable, ya que si no peta la app
 */
val UserInfoNavType = object : NavType<UserInfo>(isNullableAllowed = false) {

    /**
     * Sacamos del path el objeto UserInfo
     */
    override fun get(bundle: Bundle, key: String): UserInfo? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    /**
     * Transformamos el UserInfo que esta como String, a UserInfo otra vez
     */
    override fun parseValue(value: String): UserInfo {
        return Json.decodeFromString(Uri.decode(value))
    }

    /**
     * Convertimos el objeto UserInfo en Json para la navegacion
     */
    override fun serializeAsValue(value: UserInfo): String {
        return Uri.encode(Json.encodeToString(value))
    }

    /**
     * Guardamos el objeto en el path de la ruta pasandolo a String
     */
    override fun put(bundle: Bundle, key: String, value: UserInfo) {
        bundle.putString(key, Json.encodeToString(value))
    }
}