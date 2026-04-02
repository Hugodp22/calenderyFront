package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.InputCreation
import com.example.calenderyfront.Model.States.ProfileSettingsState
import com.example.calenderyfront.Model.States.RegisterState
import com.example.calenderyfront.Model.ViewModels.ProfileSettingsViewModel
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.galleryLauncher

@Composable
fun descriptionContent(description: String,onValueChange: (String) -> Unit,modifier: Modifier = Modifier) {
    val cantidadMaxima: Int = 150
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        )
    {
        OutlinedTextField (
            value = description,
            onValueChange = {
                //Le ponemos limite a la descripcion
                if (it.length <= cantidadMaxima) {
                    onValueChange(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text(stringResource(R.string.share_description_Mesagge))},
            maxLines = 3,
            singleLine = false,
            //Falta poner Colors a las letras
        )
    }
}

@Composable
fun SettingScreen(modifier: Modifier = Modifier,windowSize: WindowWidthSizeClass, viewModel: ProfileSettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val errorName by viewModel.errorName.collectAsState()

    //Funcion para abrir la galeria y selecionar una foto de esta
    val openGallery = galleryLauncher { uri -> uri?.let {
        viewModel.onPhotoChange(it.toString())
    }}

    val width = when(windowSize) {
        WindowWidthSizeClass.Compact -> 1F
        WindowWidthSizeClass.Medium -> 1F
        WindowWidthSizeClass.Expanded -> 1F
        else -> 1F
    }
    Box(
        modifier = modifier.fillMaxSize(),
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
                )
            PhotoUserContainer(uiState.fotoPerfil, openGallery,R.string.image_description,Modifier.size(100.dp))
            InputCreation(R.string.input_label_name, uiState.nombre, { viewModel.onNameChange(it) }, R.string.input_placeholder_empty_name,false,errorName)
            descriptionContent(uiState.descripcion,{viewModel.onDescriptionChange(it)},Modifier.fillMaxWidth(width))
            SaveButton(windowSize, onClick = {viewModel.tryChangeSettings()})

            if (stateProcess is ProfileSettingsState.Error) {
                Text(
                    text = stringResource((stateProcess as RegisterState.Error).mensaje),
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            else if (stateProcess is ProfileSettingsState.Cargando) {
                CircularProgressIndicator()
            }
        }
    }
}

