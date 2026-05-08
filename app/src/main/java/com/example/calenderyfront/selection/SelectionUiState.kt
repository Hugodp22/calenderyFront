package com.example.calenderyfront.selection

import com.example.calenderyfront.Model.DataObjects.SelectionUserChatData
import com.example.calenderyfront.Model.DataObjects.SelectionUserProfileData
import com.example.calenderyfront.Model.DataObjects.UserInfo

data class SelectionUiState (
    val userInfo: UserInfo,
    val chatOption: Boolean,
    val searchName : String = "",
    val selectionUsersChatList: List<SelectionUserChatData> = emptyList(),
    val selectionUsersProfileList: List<SelectionUserProfileData> = emptyList(),
    val lastPage: Boolean = false
)