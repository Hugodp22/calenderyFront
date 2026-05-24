package com.example.calenderyfront.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.galleryLauncher
import com.example.calenderyfront.ui.theme.BebasNeue
import com.example.calenderyfront.ui.theme.LocalCustomColors
import com.example.calenderyfront.upload.UploadState
import com.example.calenderyfront.upload.UploadViewModel

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToUpload: (UserInfo,Int, String, String) -> Unit,
    viewModel: UploadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()
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

    val size = when (windowSize) {
        WindowWidthSizeClass.Compact -> 400.dp
        WindowWidthSizeClass.Medium -> 500.dp
        WindowWidthSizeClass.Expanded -> 350.dp
        else -> 400.dp
    }

    val topPadding = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.dp
        else -> 60.dp
    }

    val openGallery = galleryLauncher { uri ->
        uri?.let { viewModel.onPhotoChange(it.toString()) }
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is UploadState.Exito) {
            onNavigateToUpload(
                (stateProcess as UploadState.Exito).userInfo,
                (stateProcess as UploadState.Exito).postId,
                uiState.fotoSubir,
                (stateProcess as UploadState.Exito).photoUrl
            )
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
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, bottom = 20.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(Modifier.padding(top = topPadding))

            Card(
                modifier = Modifier
                    .width(size)
                    .height(size)
                    .clickable { openGallery() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
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

            Button(
                onClick = { viewModel.uploadPhoto() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                ),
                enabled = enableButton,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(40.dp)
            ) {
                Text(
                    text = stringResource(R.string.next),
                    fontSize = 13.sp,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (stateProcess is UploadState.Cargando) {
                val colors = LocalCustomColors.current
                CircularProgressIndicator(
                    color = colors.spinner
                )
            }

            else if (stateProcess is UploadState.Error) {
                Text(
                    text = stringResource((stateProcess as UploadState.Error).mensaje),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}