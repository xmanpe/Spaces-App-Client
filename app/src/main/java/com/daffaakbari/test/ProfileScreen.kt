package com.daffaakbari.test

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.daffaakbari.test.session.PreferenceDatastore
import com.daffaakbari.test.session.SessionModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
data class SpaceItemProfile(
    val spaceName: String,
    val spaceUsername: String,
    val description: String,
    val usernameUser: String
)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(preferenceDatastore: PreferenceDatastore) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var totalOwnedSpace = 0

    // Take currUser Username
    var currUsername by remember { mutableStateOf("") }
    CoroutineScope(Dispatchers.IO).launch {
        preferenceDatastore.getSession().collect{
            withContext(Dispatchers.Main) {
//                Log.d("Profile", it.toString())
                currUsername = it.username
            }
        }
    }

    // Get CurrUser Data
    val db = Firebase.firestore
    db.collection("users")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("Profile", "${document.id} => ${document.data}")
                // Check Username dan Password
                if(document.data["username"].toString() == currUsername) {
                    name = document.data["name"].toString()
                    username = document.data["username"].toString()
                    email = document.data["email"].toString()
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Profile", "Error getting documents.", exception)
        }

    // Get user image
    var storage = Firebase.storage("gs://spaces-1751c.appspot.com")
    var storageRef = storage.reference
//    var imageRef: StorageReference? = storageRef.child("users")
    var collectedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    storageRef.child("users/$username.jpg").downloadUrl.addOnSuccessListener { uri->
        collectedImageUri = uri
        Log.d("TakePicture", "Berhasil")
    }.addOnFailureListener {exception->
        Log.d("TakePicture", "Gagal")
    }

    // Get all spaces
    var listSpace by remember { mutableStateOf(mutableListOf<SpaceItemProfile>()) }

    db.collection("spaces")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("Profile", "${document.id} => ${document.data}")
                listSpace.add(
                    SpaceItemProfile(
                        spaceName = document.data["name"].toString(),
                        spaceUsername = document.data["username"].toString(),
                        description = document.data["description"].toString(),
                        usernameUser = document.data["usernameUser"].toString()
                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("HomeScreen", "Error getting documents.", exception)
        }

    // Remove duplicate space
    val distinctlistSpace = listSpace.distinctBy { it.spaceUsername }

    // Get currUser total space
    for(item in distinctlistSpace) {
        if(item.usernameUser == currUsername) {
            totalOwnedSpace++
        }
    }

    fun HandleLogout() {
        CoroutineScope(Dispatchers.IO).launch {
            var modelSession = SessionModel("", "")
            preferenceDatastore.setSession(modelSession)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarTitleOnly(header = "Profile")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .width(250.dp).height(250.dp),
                painter = rememberImagePainter(collectedImageUri),
                contentDescription = null
            )
            Text(text = "Name: $name",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
            Text(text = "Username: @$username",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
            Text(text = "Email : $email",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
            Text(text = "Jumlah Space: $totalOwnedSpace",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp
            )
            Button(
                onClick = { HandleLogout() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = CircleShape,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(width = 180.dp, height = 36.dp)
                    .clip(CircleShape)
            ) {
                Text(text = "Logout")
            }
        }
    }
}