package com.example.calenderyfront.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.calenderyfront.MessageLimitContent
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.galleryLauncher
import com.example.calenderyfront.ui.theme.BebasNeue
import com.example.calenderyfront.upload.UploadState
import com.example.calenderyfront.upload.UploadViewModel

@Composable
fun PostMessageInput(
    windowSize: WindowWidthSizeClass
)
{

}

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToProfile: (UserInfo) -> Unit,
    viewModel: UploadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()
    val context = LocalContext.current
    val enableButton = stateProcess !is UploadState.Cargando && stateProcess !is UploadState.Exito

    val iconSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 150.dp
        WindowWidthSizeClass.Medium -> 160.dp
        WindowWidthSizeClass.Expanded -> 170.dp
        else -> 150.dp
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 30.sp
        WindowWidthSizeClass.Medium -> 45.sp
        WindowWidthSizeClass.Expanded -> 45.sp
        else -> 45.sp
    }

    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.8F
        WindowWidthSizeClass.Medium -> 0.8F
        WindowWidthSizeClass.Expanded -> 0.8F
        else -> 0.8F
    }

    val height = when (windowSize) {
        WindowWidthSizeClass.Compact -> 400.dp
        WindowWidthSizeClass.Medium -> 450.dp
        WindowWidthSizeClass.Expanded -> 450.dp
        else -> 400.dp
    }

    val openGallery = galleryLauncher { uri ->
        uri?.let { viewModel.onPhotoChange(it.toString()) }
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is UploadState.Exito) {
            onNavigateToProfile((stateProcess as UploadState.Exito).userInfo)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        Text(
            text = stringResource(R.string.select_image_upload),
            fontFamily = BebasNeue,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp, bottom = 20.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).height(height).clickable { openGallery() },
                colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            )
            {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
                {
                    if (uiState.fotoSubir.isNotEmpty()) {
                        AsyncImage(
                            model = uiState.fotoSubir,
                            contentDescription = stringResource(R.string.selected_image),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else {
                        Icon(
                            painter = painterResource(R.drawable.upload),
                            contentDescription = stringResource(R.string.select_photo_upload),
                            modifier = Modifier.size(iconSize),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MessageLimitContent(Modifier.fillMaxWidth(width),R.string.upload_message,"",{viewModel.onMessageChange(it)})

            Spacer(modifier = Modifier.height(32.dp))

            SaveButton(
                textButton = R.string.upload,
                windowSize = windowSize,
                onClick = { viewModel.uploadPhoto(context) },
                enable = enableButton
            )

            if (stateProcess is UploadState.Cargando) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }

            else if (stateProcess is UploadState.Error) {
                Text(
                    text = stringResource((stateProcess as UploadState.Error).mensaje),
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}