package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calenderyfront.Model.DataObjects.Home
import com.example.calenderyfront.Model.DataObjects.Login
import com.example.calenderyfront.Model.DataObjects.Redirect
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.VerifyLink
import com.example.calenderyfront.redirect.RedirectState
import com.example.calenderyfront.redirect.RedirectViewModel

@Composable
fun RedirectScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: RedirectViewModel = viewModel()
    )
{
    val stateProcess by viewModel.state.collectAsState()

    LaunchedEffect(stateProcess) {
        when (stateProcess) {
            is RedirectState.Exito -> {
                navController.navigate(Home((stateProcess as RedirectState.Exito).userInfo)) //En un futuro, ponerlo a la screen home
            }

            is RedirectState.NoLogin -> {
                navController.navigate(Login)
            }

            is RedirectState.NoValidate -> {
                navController.navigate(VerifyLink((stateProcess as RedirectState.NoValidate).userInfo))
            }
            else -> {}
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        if (stateProcess is RedirectState.Cargando) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        else if (stateProcess is RedirectState.Error) {
            Text(
                text = stringResource((stateProcess as RedirectState.Error).mensaje),
                fontSize = 14.sp,
                color = Color.Red
            )
        }
    }
}