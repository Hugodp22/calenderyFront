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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.waitingForLink.WaitingForLinkState
import com.example.calenderyfront.waitingForLink.WaitingForLinkViewModel
import com.example.calenderyfront.R
import com.example.calenderyfront.TextLink
import com.example.calenderyfront.ui.theme.BebasNeue

@Composable
fun WaitingForLinkScreen(
    modifier: Modifier = Modifier,
    onNavigateToSettings : (UserInfo) -> Unit,
    viewModel: WaitingForLinkViewModel = viewModel(),
    windowSize: WindowWidthSizeClass,
    )
{
    val stateProcess by viewModel.state.collectAsState()

    val width = when (windowSize) {
        WindowWidthSizeClass.Medium -> 0.7F
        WindowWidthSizeClass.Expanded -> 0.7F
        else -> 1f
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is WaitingForLinkState.Exito) {
            onNavigateToSettings((stateProcess as WaitingForLinkState.Exito).userInfo)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkValidation()
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
                    text = stringResource(R.string.waiting_link_title),
                    fontSize = 32.sp,
                    lineHeight = 30.sp,
                    softWrap = true,
                    fontFamily = BebasNeue,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.link_waiting_message),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (stateProcess is WaitingForLinkState.Iniciado || stateProcess is WaitingForLinkState.Error) {
                    TextLink(R.string.resend_email, { viewModel.resendEmail() }, windowSize)
                }

                if (stateProcess is WaitingForLinkState.Cargando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
