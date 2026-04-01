package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import com.example.calenderyfront.Model.States.LoginState
import com.example.calenderyfront.Model.ViewModels.LoginViewModel
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass


@Composable
fun LoginScreen(modifier: Modifier = Modifier, windowSize: WindowWidthSizeClass, viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val errorEmail by viewModel.errorEmail.collectAsState()
    val errorKeypass by viewModel.errorKeypass.collectAsState()

    val width = when (windowSize) {
        WindowWidthSizeClass.Medium -> 0.7F
        WindowWidthSizeClass.Expanded -> 0.7F
        else -> 1f
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                )

                InputCreation(R.string.input_label_email, uiState.email, { viewModel.onEmailChange(it)}, R.string.input_placeholder_empty_email,false,errorEmail)
                InputCreation(R.string.input_label_keypass, uiState.keypass, { viewModel.onKeypassChange(it) }, R.string.input_placeholder_empty_keypass,true, errorKeypass)
                SaveButton(windowSize,onClick = { viewModel.tryLogin()})

                if (stateProcess is LoginState.Error) {
                    Text(
                        text = stringResource((stateProcess as LoginState.Error).mensaje),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                else if (stateProcess is LoginState.Cargando) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

