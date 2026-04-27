package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.MessageLimitContent
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.postDataUpload.PostDataUploadState
import com.example.calenderyfront.postDataUpload.PostDataUploadViewModel

@Composable
fun PostDataUploadScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToProfile: (UserInfo) -> Unit,
    viewModel: PostDataUploadViewModel = viewModel(),
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()
    val enableButton = stateProcess !is PostDataUploadState.Cargando && stateProcess !is PostDataUploadState.Exito


    val context = LocalContext.current

    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.8F
        else -> 0.8F
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is PostDataUploadState.Exito) {
            onNavigateToProfile((stateProcess as PostDataUploadState.Exito).userInfo)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        MessageLimitContent(Modifier.fillMaxWidth(width),R.string.upload_message,uiState.message,{viewModel.onMessageChange(it)})
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(width)
        )
        {

        }
        SaveButton(R.string.upload,windowSize,{viewModel.uploadPost(context)},enableButton)
    }
}