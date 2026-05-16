package com.example.calenderyfront

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calenderyfront.ui.theme.BebasNeue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderyTopBar(
    windowSize: WindowWidthSizeClass
)
{


    // LOGO SIZE
    val logoSize = when (windowSize) {

        WindowWidthSizeClass.Compact -> 72.dp

        WindowWidthSizeClass.Medium -> 86.dp

        WindowWidthSizeClass.Expanded -> 96.dp

        else -> 72.dp
    }



    // TITLE SIZE
    val titleSize = when (windowSize) {

        WindowWidthSizeClass.Compact -> 28.sp

        WindowWidthSizeClass.Medium -> 34.sp

        WindowWidthSizeClass.Expanded -> 38.sp

        else -> 28.sp
    }



    // SPACE BETWEEN ITEMS
    val spacerWidth = when (windowSize) {

        WindowWidthSizeClass.Compact -> 8.dp

        WindowWidthSizeClass.Medium -> 10.dp

        WindowWidthSizeClass.Expanded -> 12.dp

        else -> 8.dp
    }


    CenterAlignedTopAppBar(

        title = {

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.Center
            )
            {


                // APP LOGO
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.calendary_app),
                    modifier = Modifier.size(logoSize)
                )


                Spacer(
                    modifier = Modifier.width(spacerWidth)
                )


                // APP TITLE
                Text(
                    text = stringResource(R.string.calendary_app),

                    fontFamily = BebasNeue,

                    fontSize = titleSize,

                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    )
}