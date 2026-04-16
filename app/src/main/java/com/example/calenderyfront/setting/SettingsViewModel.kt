package com.example.calenderyfront.setting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Settings
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.Model.DataObjects.UserSettings
import com.example.calenderyfront.R
import com.example.calenderyfront.RetrofitClient
import com.example.calenderyfront.errorMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class SettingsViewModel(path: SavedStateHandle): ViewModel() {
    private val userInfo = path.toRoute<Settings>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _uiState = MutableStateFlow(SettingsUiState(userInfo, "", "", ""))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Iniciado)
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _errorName = MutableStateFlow(false)
    val errorName: StateFlow<Boolean> = _errorName.asStateFlow()

    init {
        //loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.buscarDatosUsuarioPorId(userInfo.idUsuario)

                if (respuesta.isSuccessful) {
                    val configuracionUsuario = respuesta.body()

                    if (configuracionUsuario != null) {
                        _uiState.update {
                            it.copy(
                                nombre = configuracionUsuario.nombre,
                                fotoPerfil = configuracionUsuario.fotoPerfil,
                                descripcion = configuracionUsuario.descripcion
                            )
                        }
                    }
                }
                else {
                    val codigoError = respuesta.code()
                    _state.value = SettingsState.Error(errorMessages(codigoError))
                }
            }
            catch (e: Exception) {
                _state.value = SettingsState.Error(R.string.Error_Network)
            }
        }
    }

    fun onNameChange(nuevoNombre: String) {
        _errorName.value = false
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun onDescriptionChange(nuevaDescripcion: String) {
        _uiState.update { it.copy(descripcion = nuevaDescripcion) }
    }

    fun onPhotoChange(nuevaFoto: String) {
        _uiState.update { it.copy(fotoPerfil = nuevaFoto) }
    }

    fun tryChangeSettings() {
        val currentUiState = _uiState.value

        //Quitamos el mensaje de error
        if (_state.value is SettingsState.Error) {
            _state.value = SettingsState.Iniciado
        }

        if (currentUiState.nombre.isEmpty()) {
            _errorName.value = true
            _state.value = SettingsState.Error(R.string.Error_name_message)
            return
        }

        _errorName.value = false

        //Se pone la foto por defecto
        if (currentUiState.fotoPerfil.isEmpty()) {
            _uiState.update { it.copy(fotoPerfil = "https://hplwhrjrasmhwsjtawht.supabase.co/storage/v1/object/public/Avatares/Perfil_defecto.png")}
        }

        _state.value = SettingsState.Cargando
        updateProfile()
    }

    fun updateProfile() {
        val currentUiState = _uiState.value

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val datosActualizados = UserSettings(
                    nombre = currentUiState.nombre,
                    fotoPerfil = currentUiState.fotoPerfil,
                    descripcion = currentUiState.descripcion
                )

                //Falta transformar la foto de perfil en archivo para asi mandarlo bien. No se
                //faltan pruebas aun.

                val respuesta = RetrofitClient.usuarioApi.cambiarConfiguracionUsuario(datosActualizados)

                if (respuesta.isSuccessful) {
                    _state.value = SettingsState.Exito(userInfo)
                }
                else {
                    val codigoError = respuesta.code()
                    _state.value = SettingsState.Error(errorMessages(codigoError))
                }
            }
            catch (e: Exception) {
                _state.value = SettingsState.Error(R.string.Error_Network)
            }
        }
    }
}