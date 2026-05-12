package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.TextLink
import com.example.calenderyfront.login.LoginState
import com.example.calenderyfront.login.LoginViewModel
import com.example.calenderyfront.ui.theme.BebasNeue

/**
 * Funcion para cargar la pantalla de login
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToRegister : () -> Unit,
    onNavigateToHome: (UserInfo) -> Unit,
    windowSize: WindowWidthSizeClass,
    viewModel: LoginViewModel = viewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val errorEmail by viewModel.errorEmail.collectAsState()
    val errorKeypass by viewModel.errorKeypass.collectAsState()

    val enableButton = stateProcess !is LoginState.Cargando && stateProcess !is LoginState.Exito

    val width = when (windowSize) {
        WindowWidthSizeClass.Medium -> 0.7F
        WindowWidthSizeClass.Expanded -> 0.7F
        else -> 1f
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is LoginState.Exito) {
            onNavigateToHome((stateProcess as LoginState.Exito).userInfo)
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
                    .fillMaxWidth(width).
                    padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            )
            {
                Text(
                    text = stringResource(R.string.Login_title),
                    fontSize = 32.sp,
                    fontFamily = BebasNeue,
                    color = MaterialTheme.colorScheme.tertiary
                )

                InputCreation(Modifier.fillMaxWidth(0.9F),R.string.input_label_email, uiState.email, { viewModel.onEmailChange(it)}, R.string.input_placeholder_empty_email,false,errorEmail,windowSize)
                InputCreation(Modifier.fillMaxWidth(0.9F),R.string.input_label_keypass, uiState.keypass, { viewModel.onKeypassChange(it) }, R.string.input_placeholder_empty_keypass,true, errorKeypass,windowSize)
                SaveButton(R.string.Login_button,windowSize,onClick = { viewModel.tryLogin()},enableButton)

                when (stateProcess) {
                    is LoginState.Iniciado, is LoginState.Error -> {
                        TextLink(R.string.redirect_register,onNavigateToRegister,windowSize)
                }
                    else -> {
                        //No carga para evitar que le den durante la peticion
                    }
                }

                if (stateProcess is LoginState.Error) {
                    Text(
                        text = stringResource((stateProcess as LoginState.Error).mensaje),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                else if (stateProcess is LoginState.Cargando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}

