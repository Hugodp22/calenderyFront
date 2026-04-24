package com.example.calenderyfront.redirect

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Screens.RedirectScreen
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
                    //Si esta aqui, es que tiene cabecera, entonces obtenemos el userInfo
                    val respuesta = RetrofitClient.usuarioApi.buscarPerfilUsuarioPorCabecera()

                    if (respuesta.isSuccessful) {
                        val userInfo = respuesta.body()

                        if (userInfo != null) {
                            isValid(userInfo)
                        }
                    }
                    else {
                        SessionManager.clearSession(getApplication())
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
            SessionManager.clearSession(getApplication())
            _state.value = RedirectState.NoLogin
        }
    }

    fun isValid(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.validarUsuario(userInfo.idUsuario)

                if (respuesta.isSuccessful) {
                    _state.value = RedirectState.Exito(userInfo)
                }
                else {
                    _state.value = RedirectState.NoValidate(userInfo)
                }
            }
            catch (e: Exception) {
                SessionManager.clearSession(getApplication())
                _state.value = RedirectState.NoLogin
            }
        }
    }

}