package com.example.calenderyfront.postDataUpload

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.PostData
import com.example.calenderyfront.Model.DataObjects.PostDataUpload
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf

class PostDataUploadViewModel(path: SavedStateHandle): ViewModel()  {

    private val route = path.toRoute<PostDataUpload>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    )

    private val userInfo = route.userInfo
    private val postId = route.postId
    private val photoPath = route.photoPath
    private val photoUrl = route.photoUrl

    private val _uiState = MutableStateFlow(PostDataUploadUiState(userInfo, postId, photoPath, photoUrl,"",))
    val uiState: StateFlow<PostDataUploadUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<PostDataUploadState>(PostDataUploadState.Iniciado)
    val state: StateFlow<PostDataUploadState> = _state.asStateFlow()

    fun onMessageChange(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun onDateChange(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    init {

    }

    fun uploadPost(context: Context) {
        _state.value = PostDataUploadState.Cargando
        val currentUiState = _uiState.value

        if (currentUiState.date == null) {
            _state.value = PostDataUploadState.Error(R.string.Error_date)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uriGallery = currentUiState.photoPath.toUri()

                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val dateString = currentUiState.date.format(dateFormatter)

                val sendImage = sendImageToBucket(context,uriGallery,currentUiState.photoUrl)

                if (sendImage) {

                    val respuesta = RetrofitClient.publicacionApi.mandarDatosPost(
                        postData = PostData(
                            idPost = currentUiState.postId,
                            idUsuario = userInfo.idUsuario,
                            message = currentUiState.message,
                            calendarDate = dateString
                        )
                    )

                    if (respuesta.isSuccessful){
                        _state.value = PostDataUploadState.Exito(userInfo)
                    }

                    else {
                        _state.value = PostDataUploadState.Error(errorMessages(respuesta.code()))
                    }
                }

                else {
                    _state.value = PostDataUploadState.Error(R.string.Error_photo)
                }
            }
            catch (e: Exception) {
                _state.value = PostDataUploadState.Error(R.string.Error_Network)
            }
        }

    }

}