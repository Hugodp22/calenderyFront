package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Settings
import com.example.calenderyfront.Model.DataObjects.UserSettings
import com.example.calenderyfront.Model.States.SettingsState
import com.example.calenderyfront.Model.UiStates.SettingsUiState
import com.example.calenderyfront.R
import com.example.calenderyfront.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(path: SavedStateHandle): ViewModel() {

    //Obtenemos el path del controller y obtenemos el id de este
    private val settingsPath = path.toRoute<Settings>()
    private val userId: Int = settingsPath.userId

    private val _uiState = MutableStateFlow(SettingsUiState(userId, "", "", ""))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Iniciado)
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _errorName = MutableStateFlow(false)
    val errorName: StateFlow<Boolean> = _errorName.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.buscarConfiguracionPorId(userId)

                if (respuesta.isSuccessful) {
                    val configuracionUsuario = respuesta.body()

                    if (configuracionUsuario != null) {
                        _uiState.update { it.copy(
                            nombre = configuracionUsuario.nombre,
                            fotoPerfil = configuracionUsuario.fotoPerfil,
                            descripcion = configuracionUsuario.descripcion
                        )}
                    }
                }

                else {
                    val codigoError = respuesta.code()
                    val mensajeError = when (codigoError) {
                        404 -> R.string.Error_404_Message
                        500 -> R.string.Error_500_message
                        else -> R.string.Error_Unknow_Message
                    }
                    _state.value = SettingsState.Error(mensajeError)
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
                    id = userId,
                    nombre = currentUiState.nombre,
                    fotoPerfil = currentUiState.fotoPerfil,
                    descripcion = currentUiState.descripcion
                )

                //Falta transformar la foto de perfil en archivo para asi mandarlo bien. No se
                //faltan pruebas aun.

                val respuesta = RetrofitClient.usuarioApi.cambiarConfiguracionUsuario(datosActualizados)

                if (respuesta.isSuccessful) {
                    val userId = respuesta.body()

                    if (userId != null) {
                        _state.value = SettingsState.Exito(userId)
                    }
                }
                else {
                    val codigoError = respuesta.code()
                    val mensajeError = when (codigoError) {
                        404 -> R.string.Error_404_Message
                        500 -> R.string.Error_500_message
                        else -> R.string.Error_Unknow_Message
                    }
                    _state.value = SettingsState.Error(mensajeError)
                }
            }
            catch (e: Exception) {
                _state.value = SettingsState.Error(R.string.Error_Network)
            }
        }
    }

}