package com.example.calenderyfront.Screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.Model.States.RegisterState
import com.example.calenderyfront.Model.ViewModels.RegisterViewModel
import com.example.calenderyfront.R


@Composable
fun InputCreation(
    @StringRes title: Int,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholderRes: Int,
    isPassword: Boolean = false,
    error: Boolean = false,
    modifier : Modifier = Modifier
)
{
    var passwordVisible by remember { mutableStateOf(false) } //Variable para saber si se muestra o no la contrasñea

    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.Start
    )
    {
        Text(
            text = stringResource(title),
            color = if (error) Color.Red else Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = error,
            placeholder = { Text(
                text = stringResource(placeholderRes),
                color = Color.Gray)
            },
            //Si es un input de contraseña y la contraseña no esta visible, salen puntos
            //Si esta disponible, se le quita la transformacion en puntos
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,

            trailingIcon = {
                if (isPassword) {
                    //Si la contraseña es visible mostrar ojo sin tachar y viceversa
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = stringResource(R.string.alter_visibility))
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4285F4),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4285F4),
            contentColor = Color.White
        ),
        modifier = Modifier.padding(horizontal = 32.dp)
    )
    {
        Text(
            text = stringResource(R.string.btn_save),
            fontSize = 18.sp
        )
    }
}

@Composable
fun RegisterScreen(modifier : Modifier = Modifier,viewModel: RegisterViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val errorName by viewModel.errorName.collectAsState()
    val errorEmail by viewModel.errorEmail.collectAsState()
    val errorKeypass by viewModel.errorKeypass.collectAsState()

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
                    .padding(vertical = 22.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.register),
                    fontSize = 32.sp,
                )

                InputCreation(R.string.input_label_name, uiState.nombre, { viewModel.onNameChange(it) }, R.string.input_placeholder_empty_name,false,errorName)
                InputCreation(R.string.input_label_email, uiState.correo, { viewModel.onEmailChange(it)}, R.string.input_placeholder_empty_email,false,errorEmail)
                InputCreation(R.string.input_label_keypass, uiState.keypass, { viewModel.onKeypassChange(it) }, R.string.input_placeholder_empty_keypass,true, errorKeypass)
                InputCreation(R.string.input_label_keypass_confirm, uiState.keypassConfirm, { viewModel.onConfirmKeypassChange(it) }, R.string.input_placeholder_empty_keypass_confirm,true, errorKeypass)

                if (stateProcess is RegisterState.Cargando) {
                    CircularProgressIndicator()
                }

                SaveButton(onClick = { viewModel.tryLogin()})
                if (stateProcess is RegisterState.Error) {
                    Text(
                        text = stringResource((stateProcess as RegisterState.Error).mensaje),
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}