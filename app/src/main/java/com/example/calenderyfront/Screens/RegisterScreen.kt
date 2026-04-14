package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.InputCreation
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.States.RegisterState
import com.example.calenderyfront.Model.ViewModels.RegisterViewModel
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.TextLink

@Composable
fun RegisterScreen(
    modifier : Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToWaiting: (UserInfo) -> Unit,
    onNavigateToLogin: () -> Unit,
    windowSize: WindowWidthSizeClass,
    )
{

    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val errorName by viewModel.errorName.collectAsState()
    val errorEmail by viewModel.errorEmail.collectAsState()
    val errorKeypass by viewModel.errorKeypass.collectAsState()

    val width = when (windowSize) {
        WindowWidthSizeClass.Medium -> 0.7F
        WindowWidthSizeClass.Expanded -> 0.7F
        else -> 1f
    }

    //Disparador que se activara cuando el State sea Exito
    //Para obtener el usuario y mandarlo a la siguiente pantalla
    LaunchedEffect(stateProcess) {
        if (stateProcess is RegisterState.Exito) {
            onNavigateToWaiting((stateProcess as RegisterState.Exito).userInfo)
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
                modifier = Modifier.fillMaxWidth(width).
                padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            )
            {
                Text(
                    text = stringResource(R.string.register),
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )

                InputCreation(Modifier.fillMaxWidth(0.8F),R.string.input_label_name, uiState.nombre, { viewModel.onNameChange(it) }, R.string.input_placeholder_empty_name,false,errorName)
                InputCreation(Modifier.fillMaxWidth(0.8F),R.string.input_label_email, uiState.email, { viewModel.onEmailChange(it)}, R.string.input_placeholder_empty_email,false,errorEmail)
                InputCreation(Modifier.fillMaxWidth(0.8F),R.string.input_label_keypass, uiState.keypass, { viewModel.onKeypassChange(it) }, R.string.input_placeholder_empty_keypass,true, errorKeypass)
                InputCreation(Modifier.fillMaxWidth(0.8F),R.string.input_label_keypass_confirm, uiState.keypassConfirm, { viewModel.onConfirmKeypassChange(it) }, R.string.input_placeholder_empty_keypass_confirm,true, errorKeypass)
                SaveButton(windowSize,onClick = { viewModel.tryRegister()})
                TextLink(R.string.redirect_login,onNavigateToLogin)

                //Si hay un error mostramos el mensaje
                if (stateProcess is RegisterState.Error) {
                    Text(
                        text = stringResource((stateProcess as RegisterState.Error).mensaje),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                //Si esta cargando, mostramos una barra de carga por defecto
                //en futuro, mirar si podemos ver una personalizada
                else if (stateProcess is RegisterState.Cargando) {
                        CircularProgressIndicator()
                }
            }
        }
    }
}