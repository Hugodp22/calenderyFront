package com.example.calenderyfront.Screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.calenderyfront.userAuth.SessionManager

@Composable
fun ExitButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    val iconSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 30.dp
        else -> 30.dp
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    )
    {
        IconButton(
            modifier = modifier.size(iconSize),
            onClick = onClick
        )
        {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(R.drawable.exit),
                contentDescription = stringResource(R.string.exit_app),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun ButtonDialog(
    onClick: () -> Unit,
    @StringRes textButton: Int,
    colors: ButtonColors,
    windowSize: WindowWidthSizeClass
)
{
    val height = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.05F
        else -> 0.05F
    }

    val fontSizeButton = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        else -> 15.sp
    }
    
    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(height),
        contentAlignment = Alignment.BottomCenter
    )
    {
        TextButton(
            onClick = onClick,
            colors = colors
        )
        {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = stringResource(textButton),
                fontSize = fontSizeButton,
            )
        }
    }
}

@Composable
fun ExitDialog(
    windowSize: WindowWidthSizeClass,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
)
{
    val fontSizeTitle = when (windowSize) {
        WindowWidthSizeClass.Compact -> 25.sp
        else -> 25.sp
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = stringResource(R.string.exit_session),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = fontSizeTitle,
                    fontFamily = BebasNeue
                )
            }
        },
        
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Box(modifier = Modifier.weight(1f)) {
                    ButtonDialog(
                        onClick = onConfirm,
                        windowSize = windowSize,
                        textButton = R.string.accept,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                            disabledContentColor = Color.Red,
                            disabledContainerColor = Color.Gray
                        )
                    )
                }

                Spacer(Modifier.padding(end = 10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    ButtonDialog(
                        onClick = onDismiss,
                        windowSize = windowSize,
                        textButton = R.string.cancel_text,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                            disabledContentColor = Color.Red,
                            disabledContainerColor = Color.Gray
                        )
                    )
                }
            }
        },
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToProfile: (UserInfo) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val context = LocalContext.current

    val errorName by viewModel.errorName.collectAsState()

    val enableButton = stateProcess !is SettingsState.Cargando && stateProcess !is SettingsState.Exito

    var showExit by remember { mutableStateOf(false) }

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

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 32.sp
        else -> 32.sp
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
            shape = RoundedCornerShape(16.dp),
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
                    fontSize = fontSize,
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

        ExitButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 10.dp),
            onClick = { showExit = true },
            windowSize = windowSize
        )

        if (showExit) {
            ExitDialog(
                windowSize = windowSize,
                onConfirm = {
                    onNavigateToLogin()
                    SessionManager.clearSession(context)
                },
                onDismiss = {
                    showExit = false
                }
            )
        }
    }
}

