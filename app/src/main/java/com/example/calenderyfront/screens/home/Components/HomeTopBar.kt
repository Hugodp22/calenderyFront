package com.example.calenderyfront.screens.home.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calenderyfront.R

/**
 * TopBar específica del Home
 */
@Composable
fun HomeTopBar(
    onNotificationsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        /**
         * Botón notificaciones
         */
        IconButton(onClick = onNotificationsClick) {
            Icon(
                painter = painterResource(R.drawable.campana_oscuro),
                contentDescription = "notifications",
                modifier = Modifier.size(30.dp),

            )
        }

        /**
         *  Feed
         */
        Text(
            text = stringResource(R.string.feed),
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.tertiary
        )

        /**
         * Botón Chat
         */
        IconButton(onClick = onMessagesClick) {
            Icon(
                painter = painterResource(R.drawable.mensajes_oscuro),
                contentDescription = "messages",
                modifier = Modifier.size(30.dp),
            )
        }
    }
}