package com.example.calenderyfront.clients

import android.content.Context
import android.util.Log
import com.example.calenderyfront.userAuth.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

object WebSocketClient {
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()

    fun connect(context: Context) {
        if (stompClient != null) {
            return
        }

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
                        Log.d("STOMP-CALENDERY", "Conectado ✓")

                        //Me devolvera idChat, idUsuario y mensaje

                        // 1 si estas en el chat, se pone abajo con el id del usuario para saber cual es

                        // 2 si estas en chat selection, hacer un mapeo para marcar ese chat como nuevo mensaje,
                        // y poner como ultimo mensaje el mensaje recibido.

                        // 3 Si estas en cualquier otra pantalla con bottom bar, cambiar la botom bar a naranja
                        // y que cuando le des click, hacer que no sea naranja.

                        suscribeToPrivateMessage { message ->
                            Log.d("STOMP-CALENDERY", "Mensaje: $message")
                        }

                    }
                    LifecycleEvent.Type.CLOSED -> Log.d("STOMP-CALENDERY", "Desconectado")
                    LifecycleEvent.Type.ERROR -> Log.e("STOMP-CALENDERY", "Error: ${event.exception}")
                    else -> {}
                }
            }

        compositeDisposable.add(disposable)
        stompClient?.connect()
    }

    fun disconnect() {
        Log.d("STOMP-CALENDERY", "Cerrando conexion...")
        stompClient?.disconnect()
        compositeDisposable.clear()
        stompClient = null
    }

    fun suscribeToPrivateMessage(onMensaje: (String) -> Unit) {
        val disposable = stompClient!!.topic("/user/queue/mensajes")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                onMensaje(message.payload)
            }
        compositeDisposable.add(disposable)
    }

    fun sendPrivateMessage(destinatarioId: Long, texto: String) {
        val body = """{"destinatarioId": $destinatarioId, "texto": "$texto"}"""

        val disposable = stompClient!!.send("/app/chat/privado", body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Log.d("STOMP", "Mensaje privado enviado ✓") },
                { error -> Log.e("STOMP", "Error al enviar privado: $error") }
            )
        compositeDisposable.add(disposable)
    }

//    fun updateListMessages(message: String) : List<Message> {
//
//    }

}