package com.example.calenderyfront.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Selection
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.reflect.typeOf

class SelectionViewModel(path: SavedStateHandle): ViewModel() {

    private val userInfo = path.toRoute<Selection>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val chatOption = path.toRoute<Selection>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).chatOption

    private val _uiState = MutableStateFlow(SelectionUiState(userInfo,chatOption))
    val uiState: StateFlow<SelectionUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<SelectionState>(SelectionState.Iniciado)
    val state: StateFlow<SelectionState> = _state.asStateFlow()

    init {
        if (chatOption) {

        }
        else {

        }
    }

    fun onSearchChange(searchName: String) {
        _uiState.update { it.copy(searchName = searchName) }
    }

    fun loadChats() {
        val currentState = _uiState.value
    }

    fun searchUserByName() {
        val currentState = _uiState.value
    }


}