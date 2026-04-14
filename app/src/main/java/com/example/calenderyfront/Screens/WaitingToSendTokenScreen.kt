package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.States.WaitingToSendTokenState
import com.example.calenderyfront.Model.ViewModels.WaitingToSendTokenViewModel
import com.example.calenderyfront.R

@Composable
fun WaitingToSendTokenScreen(
    modifier: Modifier = Modifier,
    viewModel: WaitingToSendTokenViewModel = viewModel(),
    token: String,
    onNavigateToSettings: (UserInfo) -> Unit,
    windowSize : WindowWidthSizeClass
) {
    val stateProcess by viewModel.state.collectAsState()

    val buttonSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.5F
        else -> 0.3F
    }

    LaunchedEffect(stateProcess) {
        when (stateProcess) {
            is WaitingToSendTokenState.Exito -> {
                onNavigateToSettings((stateProcess as WaitingToSendTokenState.Exito).userInfo)
            }
            else -> {}
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
            Text(
                text = stringResource(R.string.waiting_title),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.tertiary
            )

            Button(
                modifier = Modifier.fillMaxWidth(buttonSize),
                onClick = {  }) //viewModel.onToken(token)
                {
                    Text(stringResource(R.string.retry_text))
                }

            if (stateProcess is WaitingToSendTokenState.Cargando) {
                CircularProgressIndicator()
            }

            else if (stateProcess is WaitingToSendTokenState.Error) {
                Text(
                    stringResource(R.string.error_token_text),
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}