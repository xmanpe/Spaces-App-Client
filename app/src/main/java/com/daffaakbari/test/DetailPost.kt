package com.daffaakbari.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailPost() {
    TopAppBackBarWithSearch()
    HeaderDetailPost()
}

@Composable
fun HeaderDetailPost() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
            .padding(16.dp)
    ) {
        // Logo dan keterangan angka
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.house_01), // Replace with actual image resource
                contentDescription = "Space Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Row(
                modifier = Modifier.padding(8.dp)
            ){
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "2401", fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Posts", fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(32.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "9340", fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                    Text(text = "People", fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        // Nama dan desc space
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = "UMN", fontSize = 24.sp, style = MaterialTheme.typography.titleMedium)
            Text(text = "@umnofficial", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Ayo kita ngobrol - ngobrol tentang kampus tercint kita ini! Langsung follow akun kita ya gais.",
                 style = MaterialTheme.typography.bodyMedium)
        }

        // Button
        Row(
            horizontalArrangement = Arrangement.SpaceBetween ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .padding(top = 10.dp)
        ) {
            Button(
                onClick = { /* Handle follow action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = CircleShape,
                modifier = Modifier
                    .size(width = 180.dp, height = 36.dp)
                    .clip(CircleShape)
            ) {
                Text(text = "Follow")
            }

            Button(
                onClick = { /* Handle follow action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = CircleShape,
                modifier = Modifier
                    .size(width = 180.dp, height = 36.dp)
                    .clip(CircleShape)
            ) {
                Text(text = "Information")
            }
        }
    }
}