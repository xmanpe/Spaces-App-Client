package com.daffaakbari.test

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SmallTopAppBar
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Shape
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarWithSearch("Home")
        Content(navController)
    }
}

data class SpaceItem(val spaceName: String, val description: String, val isFollowed: Boolean)

@Composable
fun Content(navController: NavHostController) {
    val listSpace = listOf(
        SpaceItem("Space Name 1", "Description 1", true),
        SpaceItem("Space Name 2", "Description 2", false),
        SpaceItem("Space Name 3", "Description 3", true),
        SpaceItem("Space Name 4", "Description 4", false),
        SpaceItem("Space Name 5", "Description 5", true),
    )

    LazyColumn {
        item {
            Text(
                text = "Nearest",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(count = listSpace.size) { index ->
            SpaceListItem(
                spaceName = listSpace[index].spaceName,
                description = listSpace[index].description,
                isFollowed = listSpace[index].isFollowed,
                navController = navController
            )
        }
    }
}

@Composable
fun SpaceListItem(spaceName: String, description: String, isFollowed: Boolean, navController: NavHostController) {
    fun NavigateToDetailSpace() {
        navController.navigate("detail") {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.LightGray, CircleShape)
            .border(2.dp, Color.Transparent, CircleShape)
            .clickable { NavigateToDetailSpace() }
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.house_01), // Replace with actual image resource
            contentDescription = "Space Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = spaceName,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        FollowButton(isFollowed = isFollowed)
    }
}

@Composable
fun FollowButton(isFollowed: Boolean, shape: Shape = CircleShape) {
    val backgroundColor = if (isFollowed) Color.White else Color.Black
    val contentColor = if (isFollowed) Color.Black else Color.White
    val border = if (isFollowed) {
        BorderStroke(1.dp, Color.Black)
    } else {
        null
    }

    Button(
        onClick = { /* Handle follow action */ },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = border,
        shape = shape,
        modifier = Modifier
            .height(36.dp)
            .clip(shape)
    ) {
        Text(text = if (isFollowed) "Followed" else "Follow")
    }
}

//JELASIN GW BUAT APA INI DAP
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen()
//}

