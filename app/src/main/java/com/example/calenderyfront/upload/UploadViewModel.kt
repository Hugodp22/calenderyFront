package com.example.calenderyfront.upload

import android.content.Context
import androidx.core.net.toUri
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
import com.example.calenderyfront.sendImageToBucket
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

    fun onMessageChange(mensaje: String) {
        _uiState.update { it.copy(mensaje = mensaje) }
    }

    fun uploadPhoto(context: Context) {
        _state.value = UploadState.Cargando
        val currentUiState = _uiState.value

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uriPhoto = currentUiState.fotoSubir

                if (uriPhoto.startsWith("content://")) {
                    val uriGallery = uriPhoto.toUri()

                    val respuesta = RetrofitClient.publicacionApi.obtenerUrlSubidaImagenPublicaciones()

                    if (respuesta.isSuccessful) {
                        val urls = respuesta.body()

                        if (urls != null) {
                            val sendImage = sendImageToBucket(context, uriGallery, urls.url)

                            if (sendImage) {
                                //uploadMessage(currentUiState)
                            }
                        }
                    }
                    else {
                        _state.value = UploadState.Error(errorMessages(respuesta.code()))
                    }
                }
            }

            catch (e: Exception) {
                _state.value = UploadState.Error(R.string.Error_Network)
            }
        }
    }

    fun uploadMessage(currentUiState: UploadUiState) {

    }
}