package com.example.calenderyfront.userAuth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SessionManager {
    private const val PREF_NAME = "UserSessionPrefs" //Nombre del archivo donde se guardara
    private const val KEY_EMAIL = "user_email" //Clave para email para usar clave valor
    private const val KEY_PASS = "user_keypass" //Lo mismo para contraseña

    /**
     * Funcion para obtener el objeto sharedPreferences (El archivo para guardar los datos)
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Funcion para guardar en el sharedPreferences los datos, sobreescribiendolos en caso de existir
     */
    fun saveSession(context: Context, email: String, pass: String) {
        getPrefs(context).edit(commit = true) {
            putString(KEY_EMAIL, email)
            putString(KEY_PASS, pass)
        }
    }

    /**
     * Funciones para obtener tanto el email como la contraseña
     */
    fun getEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_EMAIL, null)
    }

    fun getKeypass(context: Context): String? {
        return getPrefs(context).getString(KEY_PASS, null)
    }

    /**
     * Funcion para saber si existen la contraseña y el email a nivel interno
     * para que cuando te logeas por primera vez, no tengas que hacerlo otra vez
     * si al inicio lo detecta la aplicacion
     */
    fun isUserLoggedIn(context: Context): Boolean {
        return getEmail(context) != null && getKeypass(context) != null
    }

    /**
     * Funcion para borrar los datos del archivo, pensado para poner un boton de cerrar sesion
     * pero faltan pruebas
     */
    fun clearSession(context: Context) {
        getPrefs(context).edit {
            clear()
        }
    }
}