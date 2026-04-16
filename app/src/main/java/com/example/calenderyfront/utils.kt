package com.example.calenderyfront

import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.security.KeyPairGenerator

/**
 * Creacion de un input con capacidad de manejar errores
 * y contraseñas, con capacidad de ocultar el texto en caso de ser contraseña
 */
@Composable
fun InputCreation(
    modifier : Modifier = Modifier,
    @StringRes title: Int,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholderRes: Int,
    isPassword: Boolean = false,
    error: Boolean = false, //El error que va a gestionar el view model
    windowSize: WindowWidthSizeClass
)
{
    var passwordVisible by remember { mutableStateOf(false) } //Variable para saber si se muestra o no la contrasñea

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 16.sp
        WindowWidthSizeClass.Medium -> 18.sp
        WindowWidthSizeClass.Expanded -> 20.sp
        else -> 14.sp
    }

    Column(
        modifier = modifier,
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
            placeholder = {
                Text(
                text = stringResource(placeholderRes),
                    fontSize = fontSize,
                color = Color.Gray,
                maxLines = 1,
                softWrap = false,
                )
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
                errorBorderColor = Color.Red,
                cursorColor = MaterialTheme.colorScheme.tertiary
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
fun PhotoUserContainer(modifier : Modifier = Modifier,photoPath: Any?, onClick: () -> Unit,@StringRes contentDescription: Int) {
    //Usamos AsyncImage para poder cargar las imagenes
    //mediante su URL

    AsyncImage(
        model = photoPath,
        contentDescription = stringResource(contentDescription),
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .background(Color.LightGray),
        contentScale = ContentScale.Fit, //O .Crop
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = painterResource(R.drawable.errorimage) //Cambiar imagenes, que estas son de prueba
    )
}

/**
 * Funcion para crear una funcion para abrir la galeria del telefono
 * y seleccionar una foto de esta para sustituir la imagen donde este
 * esta funcion
 */
@Composable
fun galleryLauncher(onImageSelected: (Uri?) -> Unit): () -> Unit {
    //Creacion del launcher para abrir el selecionador de imagen por
    //defecto de android
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    )
    {
        //Esto se ejecuta cuando el usuario ha dejado de interactuar con la galeria
        //en caso de tener algo se sustitulle
        uri ->
        onImageSelected(uri)
    }
    return {
        //Devolvemos el launcher que abre el selector y especificamos
        //que solo se puedan escoger imagenes de momento, a futuro
        //intentar poner videos
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

/**
 * Creacion de un boton de guardado, pensado para usar para diferentes
 * casos de uso, ya sea guardar tu configuracion, iniciar sesion
 * y cosas asi
 */
@Composable
fun SaveButton(
    @StringRes textButton: Int,
    windowSize: WindowWidthSizeClass,
    onClick: () -> Unit
) {
    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.5F
        WindowWidthSizeClass.Medium -> 0.4F
        WindowWidthSizeClass.Expanded -> 0.45F
        else -> 0.3F
    }

    val height = when (windowSize) {
        WindowWidthSizeClass.Compact -> 40.dp
        WindowWidthSizeClass.Medium -> 60.dp
        WindowWidthSizeClass.Expanded -> 60.dp
        else -> 23.dp
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 18.sp
        WindowWidthSizeClass.Medium -> 30.sp
        WindowWidthSizeClass.Expanded -> 20.sp
        else -> 18.sp
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4285F4),
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(width).height(height)
    )
    {
        Text(
            text = stringResource(textButton),
            fontSize = fontSize,
            softWrap = false
        )
    }
}

/**
 * Texto clickable para mandar al link que tu quieras.
 * Pensado para texto de "¿Ya tienes cuenta?" y cosas asi
 */
@Composable
fun TextLink(
    @StringRes texto: Int,
    onClick: () -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 16.sp
        WindowWidthSizeClass.Medium -> 18.sp
        WindowWidthSizeClass.Expanded -> 20.sp
        else -> 14.sp
    }

    Text(
        text = stringResource(texto),
        color = Color(0xFF4285F4),
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable { onClick() }
    )
}

const val privateAlias = "com.calendery.app.auth_key"
/**
 * Funcion para generar clave publica y privada, mandando
 * la publica para guardar en la DB y guardando la privada a nivel
 * local para su uso
 */
fun securityKeyCreation(): String {
    val kpg = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_RSA,
        "AndroidKeyStore" // Forzamos el uso del Keystore para guardar a nivel interno
    )

    //Definimos el alias por el cual se va a guardar y
    //establecemos que se usara para encriptar
    val parameterSpec = KeyGenParameterSpec.Builder(
        privateAlias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_DECRYPT
    ).run {
        setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
        build()
    }

    kpg.initialize(parameterSpec)

    //Se guarda a nivel interno en zona segura
    val keyPair = kpg.generateKeyPair()

    //Enviamos la clave publica e indicamos que este en una sola linea con el wrap
    return Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
}

/**
 * Funcion para obtener un mensaje de error segun el codigo obtenido del back
 * para asi evitar duplicacion de codigo
 */
fun errorMessages(erroCode: Int): Int {
    val errorMessage = when(erroCode) {
        400 -> R.string.Error_400_Message
        401 -> R.string.Error_401_Message
        403 -> R.string.Error_403_Message
        404 -> R.string.Error_404_Message
        500 -> R.string.Error_500_message
        else -> R.string.Error_Unknow_Message
    }
    return errorMessage
}

