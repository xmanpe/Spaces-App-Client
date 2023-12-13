package com.daffaakbari.test

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffaakbari.test.session.PreferenceDatastore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarWithSearch("Home")
        Content(navController, preferenceDatastore)
    }
}

data class SpaceItem(
    val spaceName: String,
    val spaceUsername: String,
    val description: String,
    val isFollowed: Boolean,
    val isOwner: Boolean
)

data class SpaceFollowing(
    val spaceUsername: String,
    val followUsername: String
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Content(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {

    // Take currUser Username
    var currUsername by remember { mutableStateOf("") }
    CoroutineScope(Dispatchers.IO).launch {
        preferenceDatastore.getSession().collect{
            withContext(Dispatchers.Main) {
//                Log.d("CreateSpace", it.toString())
                currUsername = it.username
            }
        }
    }

    // Initialize firestore
    val db = Firebase.firestore

    // Get all follow
    var listFollow by remember { mutableStateOf(mutableListOf<SpaceFollowing>()) }
    db.collection("follow")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("HomeGetAllFollow", "${document.id} => ${document.data}")
                listFollow.add(
                    SpaceFollowing(
                        document.data["spaceUsername"].toString(),
                        document.data["followUsername"].toString(),
                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("HomeGetAllFollow", "Error getting documents.", exception)
        }
    // Remvoe duplicate follow
    val distinctlistFollow = listFollow.distinctBy { it.spaceUsername }

    // Create function to check if follow a space
    // Later this function used in get all spaces
    fun checkCurrUserFollowSpace(currUsername: String, spaceUsername: String): Boolean {
        for(item in distinctlistFollow) {
            if(item.followUsername == currUsername && item.spaceUsername == spaceUsername) {
                return true
            }
        }
        return false
    }

    // Get all spaces
    var listSpace by remember { mutableStateOf(mutableListOf<SpaceItem>()) }

    db.collection("spaces")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("HomeScreen", "${document.id} => ${document.data}")
                listSpace.add(
                    SpaceItem(
                        spaceName = document.data["name"].toString(),
                        spaceUsername = document.data["username"].toString(),
                        description = document.data["description"].toString(),
                        isFollowed = checkCurrUserFollowSpace(currUsername, document.data["username"].toString()),
                        isOwner = document.data["usernameUser"].toString() == currUsername
                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("HomeScreen", "Error getting documents.", exception)
        }

    // Remove duplicate space
    val distinctlistSpace = listSpace.distinctBy { it.spaceUsername }

    LazyColumn {
        items(count = distinctlistSpace.size) { index ->
            SpaceListItem(
                spaceName = distinctlistSpace[index].spaceName,
                spaceUsername = distinctlistSpace[index].spaceUsername,
                description = distinctlistSpace[index].description,
                isFollowed = distinctlistSpace[index].isFollowed,
                isOwner = distinctlistSpace[index].isOwner,
                navController = navController,
                currUsername = currUsername
            )
        }
    }
}

@Composable
fun SpaceListItem(
    spaceName: String,
    spaceUsername: String,
    description: String,
    isFollowed: Boolean,
    isOwner: Boolean,
    navController: NavHostController,
    currUsername: String
) {
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
        if(!isOwner) {
            FollowButton(isFollowed, currUsername, spaceUsername)
        }
    }
}

@Composable
fun FollowButton(
    isFollowed: Boolean,
    currUsername: String,
    spaceUsername: String,
    shape: Shape = CircleShape
) {
    Log.d("FOLLOW BUTTON", isFollowed.toString())
    fun HandleFollowSpace(username: String, currUsername: String) {
        if(username.isEmpty() || currUsername.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        val follow = hashMapOf(
            "spaceUsername" to username,
            "followUsername" to currUsername
        )

        db.collection("follow")
            .add(follow)
            .addOnSuccessListener { documentReference ->
//                Log.d("HandleFollow", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
//                Log.w("HandleFollow", "Error adding document", e)
            }
    }

    val backgroundColor = if (isFollowed) Color.White else Color.Black
    val contentColor = if (isFollowed) Color.Black else Color.White
    val border = if (isFollowed) {
        BorderStroke(1.dp, Color.Black)
    } else {
        null
    }

    Button(
        onClick = { HandleFollowSpace(spaceUsername, currUsername) },
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

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen()
//}

