package com.example.calenderyfront.upload

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Settings
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class UploadViewModel(path: SavedStateHandle): ViewModel() {

    private val userInfo = path.toRoute<Settings>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _uiState = MutableStateFlow(UploadUiState(userInfo, "",""))
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<UploadState>(UploadState.Iniciado)
    val state: StateFlow<UploadState> = _state.asStateFlow()

    fun onPhotoChange(nuevaFoto: String) {
        _uiState.update { it.copy(fotoSubir = nuevaFoto) }
    }

    fun uploadPhoto() {
        _state.value = UploadState.Cargando

        val currentUiState = _uiState.value

        val uriPhoto = currentUiState.fotoSubir

        if (!uriPhoto.startsWith("content://")) {
            _state.value = UploadState.Error(R.string.Error_no_photo)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.obtenerUrlSubidaImagenPublicaciones()

                if (respuesta.isSuccessful) {
                    val postData = respuesta.body()

                    if (postData != null) {
                        _state.value = UploadState.Exito(userInfo, postData.idPost, postData.url)
                    }
                }
                else {
                    _state.value = UploadState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = UploadState.Error(R.string.Error_Network)
            }
        }
    }

    fun restartState() {
        _state.value = UploadState.Iniciado
    }
}
