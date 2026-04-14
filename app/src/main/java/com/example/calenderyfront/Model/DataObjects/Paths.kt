package com.example.calenderyfront.Model.DataObjects

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
object Register

@Serializable
object Login

@Serializable
data class Settings(val userInfo: UserInfo)

@Serializable
data class VerifyLink(
    val userInfo: UserInfo
)

//NavType personalizado para que no pete al pasar un UserInfo

val UserInfoNavType = object : NavType<UserInfo>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): UserInfo? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): UserInfo {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: UserInfo): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: UserInfo) {
        bundle.putString(key, Json.encodeToString(value))
    }
}