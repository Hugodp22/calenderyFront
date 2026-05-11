package com.example.calenderyfront.observer

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.calenderyfront.clients.WebSocketClient

class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        // La app vuelve al primer plano
        WebSocketClient.appInForeground = true

        //Si ya esta validado
        if (WebSocketClient.userValid) {
            WebSocketClient.connect(context)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        //La app esta en segundo plano
        WebSocketClient.appInForeground = false
    }
}