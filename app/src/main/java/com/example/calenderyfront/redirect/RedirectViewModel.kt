package com.example.calenderyfront.redirect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.userAuth.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RedirectViewModel(application: Application) : AndroidViewModel(application){

    private val _state = MutableStateFlow<RedirectState>(RedirectState.Cargando)
    val state: StateFlow<RedirectState> = _state.asStateFlow()

    init {
        isLogin()
    }

    fun isLogin() {
        if (SessionManager.isUserLoggedIn(getApplication())) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    //Si esta aqui, es que tiene cabecera, entonces la cuenta existe, pero no sabemos
                    //si esta validada o no, asi que la validamos.

                    val email = SessionManager.getEmail(getApplication())?.trim()

                    if (email != null) {
                        val respuesta = RetrofitClient.usuarioApi.validarUsuarioPorCorreo(email)

                        if (respuesta.isSuccessful) {
                            val userValidation = respuesta.body()

                            if (userValidation != null) {

                                if (userValidation.enable) {
                                    _state.value = RedirectState.Exito(userValidation.userInfo)
                                }

                                else {
                                    _state.value = RedirectState.NoValidate(userValidation.userInfo)
                                }
                            }
                        }
                        else {
                            SessionManager.clearSession(getApplication())
                            _state.value = RedirectState.NoLogin
                        }
                    }
                    else {
                        _state.value = RedirectState.NoLogin
                    }
                }
                catch (e: Exception) {
                    SessionManager.clearSession(getApplication())
                    _state.value = RedirectState.NoLogin
                }
            }
        }

        else {
            _state.value = RedirectState.NoLogin
        }
    }

}