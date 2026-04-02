package com.example.calenderyfront

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

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

/**
 * Funcion preparada para crear contenedores de imagenes con diferentes funciones
 * pensado para usar tanto para cambiar foto como para ampliar la foto en caso
 * de que quieras verla claro.
 */
@Composable
fun PhotoUserContainer(photoPath: Any?, onClick: () -> Unit,@StringRes contentDescription: Int, modifier : Modifier = Modifier) {
    AsyncImage(
        model = photoPath,
        contentDescription = stringResource(contentDescription),
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .background(Color.LightGray),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_launcher_background), //Image while loadign
        error = painterResource(R.drawable.ic_launcher_foreground) //Error image if there is an error
    )
}

/**
 * Funcion para crear una funcion para abrir la galeria del telefono
 * y seleccionar una foto de esta para sustituir la imagen donde este
 * esta funcion
 */
@Composable
fun galleryLauncher(onImageSelected: (Uri?) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    )
    {
            uri ->
        onImageSelected(uri)
    }
    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

@Composable
fun SaveButton(windowSize: WindowWidthSizeClass, onClick: () -> Unit) {
    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.5F
        else -> 0.3F
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4285F4),
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(width)
    )
    {
        Text(
            text = stringResource(R.string.btn_save),
            fontSize = 20.sp
        )
    }
}

