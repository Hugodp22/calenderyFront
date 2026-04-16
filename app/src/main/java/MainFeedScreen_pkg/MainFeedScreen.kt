package MainFeedScreen_pkg

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.calenderyfront.R

// ----------------------
// MAIN SCREEN
// ----------------------

@Composable
fun MainFeedScreen() {

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { padding ->

        FeedContent(
            modifier = Modifier.padding(padding)
        )
    }
}

// ----------------------
// TOP BAR
// ----------------------

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A0E5C))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // NOTIFICATION
        IconButton(onClick = { }) {
            IconButton(onClick = { }) {
                Image(
                    painter = painterResource(id = R.drawable.campana_oscuro),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        // SELECTOR FEED
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Feed", color = Color.White)

            Spacer(modifier = Modifier.width(4.dp))

            Image(
                painter = painterResource(id = R.drawable.flecha_oscuro),
                contentDescription = "Notificaciones",
                modifier = Modifier
                    .size(17.dp)
                    .rotate(90f)
            )
        }

        //  MESSAGE
        IconButton(onClick = { }) {
            Image(
                painter = painterResource(id = R.drawable.mensajes_oscuro),
                contentDescription = "Notificaciones",
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

// ----------------------
// FEED CONTENT
// ----------------------

@Composable
fun FeedContent(modifier: Modifier = Modifier) {

    val Posts = List(10) { index ->
        PostUiModel(
            userName = "Usuario $index",
            text = " Vacaciones en Murcia",
            hasImage = index % 2 == 0
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2D0A3A)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(Posts) { post ->
            PostCard(post)
        }
    }
}

// ----------------------
// MODEL
// ----------------------
data class PostUiModel(
    val userName: String,
    val text: String,
    val hasImage: Boolean
)

// ----------------------
// POST CARD
// ----------------------

@Composable
fun PostCard(post: PostUiModel) {

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6A0572)
        ),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            // HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // PROFILE PICTURE
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = post.userName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "@PersonasMencionadas",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }

                // MENU
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.menu_oscuro),
                        contentDescription = "menu",
                        modifier = Modifier
                            .size(30.dp)
                            .rotate(90f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TEXTE
            Text(
                text = post.text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )


            if (post.hasImage) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.DarkGray)
                )
            }
        }
    }
}

// ----------------------
// BOTTOM BAR
// ----------------------

@Composable
fun BottomBar() {

    Box {

        NavigationBar(
            containerColor = Color(0xFF6A0572)
        ) {

            // HOME BUTTON
            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.home_claro),
                        contentDescription = "Home",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            // LENS BUTTON
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.lupa_oscuro),
                        contentDescription = "Search",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            // CALENDAR BUTTON
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.calendario_oscuro),
                        contentDescription = "Calendario",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            // PROFILE BUTTON
            NavigationBarItem(
                selected = false,
                onClick = { },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.profile_oscuro),
                        contentDescription = "Profile",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        }

        // BOTÓN CENTRAL
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 28.sp)
            // TODO: Icono crear post
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainFeedScreenPreview() {
    MainFeedScreen()
}