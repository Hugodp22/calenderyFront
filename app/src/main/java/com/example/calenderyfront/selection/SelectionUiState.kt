package com.example.calenderyfront.selection

import com.example.calenderyfront.Model.DataObjects.SelectionUserChatData
import com.example.calenderyfront.Model.DataObjects.SelectionUserProfileData
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class SelectionUiState (
    val userInfo: UserInfo,
    val chatOption: Boolean,
    val searchName : String = "",
    val selectionContactsList: List<SelectionUserChatData> = emptyList(),
    val selectionProfilesList: List<SelectionUserProfileData> = emptyList(),
    val lastPage: Boolean = false
)