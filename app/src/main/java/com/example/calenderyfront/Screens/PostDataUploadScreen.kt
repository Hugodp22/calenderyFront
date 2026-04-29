package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.MessageLimitContent
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.SaveButton
import com.example.calenderyfront.postDataUpload.PostDataUploadState
import com.example.calenderyfront.postDataUpload.PostDataUploadViewModel
import com.example.calenderyfront.ui.theme.BebasNeue
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class) // Necesario para DatePicker en Material 3
@Composable
fun PostDataUploadScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToProfile: (UserInfo) -> Unit,
    viewModel: PostDataUploadViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()
    val enableButton = stateProcess !is PostDataUploadState.Cargando && stateProcess !is PostDataUploadState.Exito

    //Sobreexcribimos la funcion de SelectableDates para que no permita
    //seleccionar un dia que no sea el actual
    val dateValidator = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = dateValidator
    )

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 40.sp
        else -> 40.sp
    }

    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 1F
        else -> 0.8F
    }

    val messageWidth = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.9F
        else -> 0.8F
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is PostDataUploadState.Exito) {
            onNavigateToProfile((stateProcess as PostDataUploadState.Exito).userInfo)
        }
    }

    //Cuando selecciones una fecha en el datePicker, se formatea a LocalDate para su uso
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { miliSeconds ->
            val localDate = Instant.ofEpochMilli(miliSeconds).atZone(ZoneId.of("UTC")).toLocalDate()

            viewModel.onDateChange(localDate)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
    {
        Text(
            modifier = Modifier.padding(top = 30.dp),
            text = stringResource(R.string.date_selection),
            fontSize = fontSize,
            fontFamily = BebasNeue,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(width)
        )
        {
            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF2D4D96),
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = Color(0xFF1A2F5E),
                    todayContentColor = MaterialTheme.colorScheme.tertiary,
                    titleContentColor = MaterialTheme.colorScheme.tertiary,
                    headlineContentColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MessageLimitContent(
            modifier = Modifier.fillMaxWidth( messageWidth),
            placeHolder = R.string.upload_message,
            description = uiState.message,
            onValueChange = { viewModel.onMessageChange(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        SaveButton(R.string.upload, windowSize, { viewModel.uploadPost(context) }, enableButton)

        Spacer(modifier = Modifier.height(64.dp))

        if (stateProcess is PostDataUploadState.Cargando) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        else if (stateProcess is PostDataUploadState.Error) {
            Text(
                text = stringResource((stateProcess as PostDataUploadState.Error).mensaje),
                fontSize = 14.sp,
                color = Color.Red
            )
        }
    }
}