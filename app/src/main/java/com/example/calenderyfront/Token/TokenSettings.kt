package com.example.calenderyfront.Token


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        //Guardamos el archivo llamado user_prefs y con preferencesDataStore
        //Nos aseguramos de que solo exista una instancia de este archivo en toda la app
        //A nivel local claro
        private val Context.dataStore by preferencesDataStore(name = "user_prefs")

        //Creamos la etiqueta unica con la que se identificara el token
        val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun saveToken(token: String) {
        //Usamos la clave para guardar su valor (El token) en local,
        //con una suspend para no romper el hilo principal
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    //Para borrar el token cuando alguien quiera cerrar sesion
    //No se si lo pondremos pero por si acaso
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    //Buscamos el token. Si el token cambia en el archivo,
    //cualquier parte de la app recibirá el nuevo valor al instante.
    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
}