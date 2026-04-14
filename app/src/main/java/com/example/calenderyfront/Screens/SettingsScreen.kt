package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.calenderyfront.Model.States.SettingsState
import com.example.calenderyfront.Model.States.RegisterState
import com.example.calenderyfront.Model.ViewModels.SettingsViewModel
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.galleryLauncher

/**
 * Creacion del contenedor para poner descripcion amplia, con limite configurable
 * para que el usuario no se pase demasiado
 */
@Composable
fun DescriptionContent( modifier: Modifier = Modifier,description: String, onValueChange: (String) -> Unit,limite : Int = 150) {
    val cantidadMaxima: Int = limite

    OutlinedTextField (
        value = description,
        onValueChange = {
            //Le ponemos limite a la descripcion
            if (it.length <= cantidadMaxima) {
                onValueChange(it)
            } },
        modifier = modifier.height(120.dp),
        placeholder = { Text(stringResource(R.string.share_description_Mesagge))},
        maxLines = 3,
        singleLine = false,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = Color(0xFF4285F4),
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            unfocusedTextColor = Color.Gray,
            focusedTextColor = MaterialTheme.colorScheme.tertiary
        )
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    viewModel: SettingsViewModel = viewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val errorName by viewModel.errorName.collectAsState()

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
            val userId = (stateProcess as SettingsState.Exito).userInfo
            //onNavigateToProfile(userInfo) seria
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
                    color = MaterialTheme.colorScheme.tertiary
                )

                PhotoUserContainer(Modifier.size(containerSize),uiState.fotoPerfil, openGallery,R.string.image_description)
                InputCreation(Modifier.fillMaxWidth(width),R.string.input_label_name, uiState.nombre, { viewModel.onNameChange(it) }, R.string.input_placeholder_empty_name,false,errorName)
                DescriptionContent(Modifier.fillMaxWidth(width),uiState.descripcion,{viewModel.onDescriptionChange(it)})
                //Mirar si poner mas cosas por que lo veo vacio
                SaveButton(windowSize, onClick = {viewModel.tryChangeSettings()})

                if (stateProcess is SettingsState.Error) {
                    Text(
                        text = stringResource((stateProcess as RegisterState.Error).mensaje),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                else if (stateProcess is SettingsState.Cargando) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

