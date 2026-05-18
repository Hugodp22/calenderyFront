package com.example.calenderyfront.clients

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.MessageToSend
import com.example.calenderyfront.userAuth.SessionManager
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Credentials
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

object WebSocketClient {
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private var userWantsConnect = false //Variable para saber si se quiere reconectar o no

    var userValid = false //Variable para saber si el usuario ya esta validado

    var appInForeground = false //Variable para saber si estamos en primer plano

    private val _messageFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messageFlow = _messageFlow.asSharedFlow()

    fun userValidation() {
        userValid = true
    }

    fun connect(context: Context) {

        if (stompClient?.isConnected == true) {
            return
        }

        userWantsConnect = true

        val email = SessionManager.getEmail(context)
        val keypass = SessionManager.getKeypass(context)

        if (email.isNullOrBlank() || keypass.isNullOrBlank()) {
            return
        }

        val credentials = Credentials.basic(email, keypass)

        stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            "wss://calenderyback.onrender.com/ws-endpoint",
            mapOf("Authorization" to credentials)
        )

        val disposable = stompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED ->  {
                        Log.d("STOMP-CALENDERY", "Conectado")
                        userWantsConnect = true

                        suscribeToPrivateMessage { message ->
                            Log.d("STOMP-CALENDERY", "Mensaje: $message")

                            //Me devolvera idChat, idUsuario y mensaje

                            // 1 si estas en el chat, se pone abajo con el id del usuario para saber cual es

                            // 2 si estas en chat selection, hacer un mapeo para marcar ese chat como nuevo mensaje,
                            // y poner como ultimo mensaje el mensaje recibido.

                            // 3 Si estas en cualquier otra pantalla con bottom bar, cambiar la botom bar a naranja
                            // y que cuando le des click, hacer que no sea naranja.
                        }
                    }

                    LifecycleEvent.Type.ERROR, LifecycleEvent.Type.CLOSED -> {
                        Log.d("STOMP-CALENDERY", "Desconectado")
                        if (userWantsConnect && appInForeground) {
                            tryToReconect(context)
                        }
                    }
                    else -> {}
                }
            }
        compositeDisposable.add(disposable)
        stompClient?.connect()
    }

     fun tryToReconect(context: Context) {
         if (stompClient?.isConnected == true) {
             Log.d("STOMP-CALENDERY", "Ya conectado, no hace falta reconectar")
             return
         }

         if (!userWantsConnect) {
             Log.d("STOMP-CALENDERY", "El usuario no quiere estar conectado, ignorando...")
             return
         }

         Log.d("STOMP-CALENDERY", "Usuario volvio, reconectando...")
         cleanClient()
         connect(context)
    }

    suspend fun checkPendingMessages(): Boolean {
        val respuesta = RetrofitClient.chatApi.comprobarMensajesPendientes()
        return if (respuesta.isSuccessful) {
            val pendientes = respuesta.body()
            pendientes ?: false
        }
        else {
            false
        }
    }

    private fun cleanClient() {
        compositeDisposable.clear()
        stompClient?.disconnect()
        stompClient = null
        Log.d("STOMP-CALENDERY", "Cliente limpiado")
    }

    fun disconnect() {
        Log.d("STOMP-CALENDERY", "Cerrando conexion...")
        userWantsConnect = false
        cleanClient()
    }

    fun suscribeToPrivateMessage(onMensaje: (String) -> Unit) {
        val disposable = stompClient!!.topic("/user/queue/mensajes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                onMensaje(message.payload)
                _messageFlow.tryEmit(message.payload)
            }
        compositeDisposable.add(disposable)
    }

    fun sendPrivateMessage(messageToSend: MessageToSend) {
        val gson = Gson()
        val jsonBody = gson.toJson(messageToSend)

        val disposable = stompClient!!.send("/app/chat.sendPrivate", jsonBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Log.d("STOMP-CALENDERY", "Mensaje privado enviado ✓") },
                { error -> Log.e("STOMP-CALENDERY", "Error al enviar privado: $error") }
            )
        compositeDisposable.add(disposable)
    }
}