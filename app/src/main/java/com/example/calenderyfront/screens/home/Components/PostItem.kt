package com.example.calenderyfront.screens.home.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.calenderyfront.Model.dataObjects.PublicacionHome


@Composable
fun PostItem(post: PublicacionHome) {

    Column {

        /**
         * HEADER (usuario)
         */
        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = post.fotoUsuario,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = post.nombreUsuario,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        /**
         * TEXTO
         */
        post.mensaje?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        /**
         * IMAGEN
         */
        if (post.fotoUsuario.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = post.fotoUsuario,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

