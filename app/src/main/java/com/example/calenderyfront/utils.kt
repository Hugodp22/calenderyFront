package com.example.calenderyfront

import android.content.Context
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.calenderyfront.clients.PhotoClient
import com.example.calenderyfront.userAuth.SessionManager
import okhttp3.Credentials
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.security.KeyPairGenerator
import java.security.KeyStore

const val pageSize = 6
const val rowProfilePostSize = 3

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
            .background(MaterialTheme.colorScheme.primary),
        contentScale = ContentScale.Crop, //O .Crop
        placeholder = painterResource(R.drawable.ic_launcher_background),
        error = painterResource(R.drawable.errorimage) //Cambiar imagenes, que estas son de prueba
    )
}

@Composable
fun ExpandedPhotoProfile(
    photoPath: Any?,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) } //

    Dialog(
        onDismissRequest = onDismiss, //Para cuando le des atras, que se ponga en false el boolean que lo controle en la screen
        properties = DialogProperties(
            usePlatformDefaultWidth = false, //Para que no ocupe toda la pantalla
            dismissOnBackPress = true,
            dismissOnClickOutside = false //Evita que pete si le das en los margenes
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        )
        {
            AsyncImage(
                model = photoPath,
                contentDescription = stringResource(R.string.dialog_image_Message),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x, //Para mover la imagen estando ampliada
                        translationY = offset.y
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f) //Limitamos el zoom entre 1 y 5

                            //Solo permitimos mover la imagen si hay zoom
                            if (scale > 1f) {
                                offset += pan * scale
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    },
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.errorimage)
            )
        }
    }
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
 * Creacion de un RequestBody mediante una Uri obtenida por galeria, para mandarla al bucket la foto
 */
fun createUriRequestBody(context: Context, uri: Uri, mediaType: MediaType?): RequestBody {
    //Creamos un RequestBody
    return object : RequestBody() {
        //Sobreescribimos la funcion de contentype para que maneje mediaType
        override fun contentType() = context.contentResolver.getType(uri)?.toMediaTypeOrNull()

        //Sobreescribimos para leer directamente desde la direccion del uri, los bytes
        //directamente
        override fun writeTo(sink: BufferedSink) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                sink.writeAll(inputStream.source())
            }
        }
    }
}

fun sendImageToBucket(context: Context, uriImage: Uri, urlBucket: String): Boolean {
    val mediaType = "image/jpeg".toMediaTypeOrNull()
    val requestBody = createUriRequestBody(context, uriImage, mediaType)

    val request = Request.Builder()
        .url(urlBucket)
        .put(requestBody)

    val email = SessionManager.getEmail(context)
    val keypass = SessionManager.getKeypass(context)

    if (!email.isNullOrBlank() && !keypass.isNullOrBlank()) {
        val head = Credentials.basic(email,keypass,Charsets.UTF_8)
        request.header("Authorization", head)

    }

    PhotoClient.client.newCall(request.build()).execute().use { respuesta ->
        return respuesta.isSuccessful //Dara error si no tiene cabecera
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
    onClick: () -> Unit,
    enable: Boolean = true
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
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 30.sp
        WindowWidthSizeClass.Expanded -> 20.sp
        else -> 18.sp
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4285F4),
            contentColor = Color.White,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color(0xFF153870)
        ),
        enabled = enable,
        modifier = Modifier
            .fillMaxWidth(width)
            .height(height)
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

/**
 * Funcion para generar clave publica y privada, mandando
 * la publica para guardar en la DB y guardando la privada a nivel
 * local para su uso
 */
fun securityKeyCreation(userId: Int): String {
    val privateAlias = "com.calendery.app.auth_key_$userId"
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
 * Funcion para borrar todas las claves generadas y que no me explote el ordenador
 * al hacer tantas pruebas
 */
fun deleteAllKeys() {
    try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val aliases = keyStore.aliases() // Lista de todos los alias

        while (aliases.hasMoreElements()) {
            val alias = aliases.nextElement()
            keyStore.deleteEntry(alias)
            println("Clave eliminada: $alias")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
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

