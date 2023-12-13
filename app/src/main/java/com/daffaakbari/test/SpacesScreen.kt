package com.daffaakbari.test

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.daffaakbari.test.session.PreferenceDatastore
import com.daffaakbari.test.session.SessionModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class OwnedSpaceItem(
    val spaceName: String,
    val spaceUsername: String,
    val description: String,
    val navController: NavHostController
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SpacesScreen(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {
    // Function to navigate to create space
    fun NavigateToCreateSpaces() {
        navController.navigate("CreateSpace") {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    // Take currUser Username
    var currUsername by remember { mutableStateOf("") }
    CoroutineScope(Dispatchers.IO).launch {
        preferenceDatastore.getSession().collect{
            withContext(Dispatchers.Main) {
                Log.d("CreateSpace", it.toString())
                currUsername = it.username
            }
        }
    }

    // Get all spaces
    var listOwnedSpace by remember { mutableStateOf(mutableListOf<OwnedSpaceItem>()) }
    val db = Firebase.firestore
    db.collection("spaces")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.d("SPACES", "${document.id} => ${document.data}")
                // Check if currUser have a space
                if(document.data["usernameUser"].toString() == currUsername) {
                    listOwnedSpace.add(
                        OwnedSpaceItem(
                            document.data["name"].toString(),
                            document.data["username"].toString(),
                            document.data["description"].toString(),
                            navController
                        )
                    )
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("SPACES", "Error getting documents.", exception)
        }

    // Remove duplicate space
    val distinctListOwnedSpace = listOwnedSpace.distinctBy { it.spaceUsername }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarWithSearch("Spaces")
        if(distinctListOwnedSpace.size > 0) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = distinctListOwnedSpace.size) { index ->
                    OwnedSpaceListItem(
                        spaceName = distinctListOwnedSpace[index].spaceName,
                        description = distinctListOwnedSpace[index].description,
                        navController = distinctListOwnedSpace[index].navController
                    )
                }
                item {
                    Button(
                        onClick = { NavigateToCreateSpaces() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(16.dp)
                            .height(36.dp)
                            .clip(CircleShape)
                    ) {
                        Text(text = "New Space")
                    }
                }
            }
        }
        else {
            NoSpaceCreated(navController)
        }
    }
}

@Composable
fun OwnedSpaceListItem(spaceName: String, description: String, navController: NavHostController) {
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
    }
}

@Composable
fun NoSpaceCreated(navController: NavHostController) {
    fun NavigateToCreateSpace() {
        navController.navigate("CreateSpace") {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Space Created.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Let people talk your topic by pressing the button below",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = { NavigateToCreateSpace() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black),
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .height(36.dp)
                .clip(CircleShape)
        ) {
            Text(text = "New Space")
        }
    }
}