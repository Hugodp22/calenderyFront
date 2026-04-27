package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.InputCreation
import com.example.calenderyfront.MessageLimitContent
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.galleryLauncher
import com.example.calenderyfront.settings.SettingsState
import com.example.calenderyfront.settings.SettingsViewModel
import com.example.calenderyfront.ui.theme.BebasNeue

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToProfile: (UserInfo) -> Unit,
    viewModel: SettingsViewModel = viewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val context = LocalContext.current

    val errorName by viewModel.errorName.collectAsState()

    val enableButton = stateProcess !is SettingsState.Cargando && stateProcess !is SettingsState.Exito

    //Funcion para abrir la galeria y selecionar una foto de esta
    val openGallery = galleryLauncher { uri -> uri?.let {
        viewModel.onPhotoChange(it.toString())
    }}

    val width = when(windowSize) {
        WindowWidthSizeClass.Compact -> 0.9F
        WindowWidthSizeClass.Medium -> 0.7F
        WindowWidthSizeClass.Expanded -> 0.7F
        else -> 0.9F
    }

    val containerSize = when(windowSize) {
        WindowWidthSizeClass.Compact -> 100.dp
        WindowWidthSizeClass.Medium -> 115.dp
        WindowWidthSizeClass.Expanded -> 120.dp
        else -> 100.dp
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is SettingsState.Exito) {
            onNavigateToProfile((stateProcess as SettingsState.Exito).userInfo)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth(width)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            )
            {
                Text(
                    text = stringResource(R.string.Settings_Title),
                    fontSize = 32.sp,
                    fontFamily = BebasNeue,
                    color = MaterialTheme.colorScheme.tertiary
                )

                PhotoUserContainer(Modifier.size(containerSize),uiState.fotoPerfil, openGallery,R.string.image_description)
                InputCreation(Modifier.fillMaxWidth(width),R.string.input_label_name, uiState.nombre, { viewModel.onNameChange(it) }, R.string.input_placeholder_empty_name,false,errorName,windowSize)
                MessageLimitContent(Modifier.fillMaxWidth(width),R.string.share_description_Mesagge,uiState.descripcion,{viewModel.onDescriptionChange(it)})
                SaveButton(R.string.btn_save, windowSize, onClick = {viewModel.tryChangeSettings(context)},enableButton)

                if (stateProcess is SettingsState.Error) {
                    Text(
                        text = stringResource((stateProcess as SettingsState.Error).mensaje),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                else if (stateProcess is SettingsState.Cargando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}

