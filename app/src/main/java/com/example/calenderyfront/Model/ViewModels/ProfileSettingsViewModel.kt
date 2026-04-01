package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.UserSettings
import com.example.calenderyfront.Model.States.ProfileSettingsState
import com.example.calenderyfront.Model.UiStates.ProfileSettingsUiState
import com.example.calenderyfront.R
import com.example.calenderyfront.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSettingsViewModel: ViewModel() {
    //Aqui irian los datos del usuario actual, de momento por defecto
    private val _uiState = MutableStateFlow(ProfileSettingsUiState("","",""))
    val uiState: StateFlow<ProfileSettingsUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<ProfileSettingsState>(ProfileSettingsState.Iniciado)
    val state: StateFlow<ProfileSettingsState> = _state.asStateFlow()

    private val _errorName = MutableStateFlow(false)
    val errorName : StateFlow<Boolean> = _errorName.asStateFlow()

    fun onNameChange(nuevoNombre: String) {
        _errorName.value = false
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun tryChangeSettings() {
        val currentUiState = _uiState.value

        if (_state.value is ProfileSettingsState.Error) {
            _state.value = ProfileSettingsState.Iniciado
        }

        if (currentUiState.nombre.isEmpty()) {
            _errorName.value = true
            _state.value = ProfileSettingsState.Error(R.string.Error_name_message)
            return
        }

        _errorName.value = false

        if (currentUiState.fotoPerfil.isEmpty()) {
            _uiState.update { it.copy(fotoPerfil = "https://hplwhrjrasmhwsjtawht.supabase.co/storage/v1/object/public/Avatares/Perfil_defecto.png")}
        }

        updateProfile()
    }

    fun updateProfile() {
        val currentUiState = _uiState.value

        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Hacer que reciba id para indicarle al back que id updatear
                val datosActualizados = UserSettings(
                    id = 1, //Lo sacariamos del usuario que nos pasaron para entrar en la configuracion
                    nombre = currentUiState.nombre,
                    fotoPerfil = currentUiState.fotoPerfil,
                    descripcion = currentUiState.descripcion
                )

                val respuesta = RetrofitClient.usuarioApi.cambiarConfiguracionUsuario(datosActualizados)

                if (respuesta.isSuccessful) {
                    val usuarioActualizado = respuesta.body() //E ir al perfil
                }
                else {
                    val codigoError = respuesta.code()
                    val mensajeError = when (codigoError) {
                        404 -> R.string.Error_404_Message
                        500 -> R.string.Error_500_message
                        else -> R.string.Error_Unknow_Message
                    }
                    _state.value = ProfileSettingsState.Error(mensajeError)
                }
            }
            catch (e: Exception) {
                _state.value = ProfileSettingsState.Error(R.string.Error_Network)
            }
        }
    }

}