package com.daffaakbari.test

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.daffaakbari.test.session.PreferenceDatastore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


data class posts (
    val document_id :String,
    val description : String,
    val usernameSpace : String,
    val usernameUser : String,
    val image : String
)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FollowingScreen(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {
    var listFollowedPosts by remember { mutableStateOf(mutableListOf<posts>()) }
    val db = Firebase.firestore

    //get the current user session
    var currUsername by remember { mutableStateOf("") }
    CoroutineScope(Dispatchers.IO).launch {
        preferenceDatastore.getSession().collect{
            withContext(Dispatchers.Main) {
                currUsername = it.username
            }
        }
    }
    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.d("document_id", "${document.id} => ${document.data}")
                // Check if currUser have a space
                if(document.data["usernameUser"].toString() == currUsername) {
                    listFollowedPosts.add(
                        posts(
                            document.id,
                            document.data["name"].toString(),
                            document.data["username"].toString(),
                            document.data["description"].toString(),
                            document.data["image"].toString()
                        )
                    )
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("SPACES", "Error getting documents.", exception)
        }
    var distinctListFollowedPosts = listFollowedPosts.distinctBy { it.document_id }
    var imageRef by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        TopAppBarWithSearch("Following")
        for (item in distinctListFollowedPosts) {
            LaunchedEffect(key1 = imageRef) {
                imageRef = getPost(item.image)
            }
            ListFollowedSpace(item,imageRef)

        }

    }
}

@Composable
fun ListFollowedSpace(listFollowedposts: posts, image :String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            Row(){
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = "Space Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Column() {
                    Row (){
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "@Joshua anjing")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "1h")

                    }
                    Text(text ="Healing ke bali dulu gk sih")
                    Image(
                        painter = rememberAsyncImagePainter(image),
                        contentDescription = "lala",
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp)
                            .width(200.dp)
                    )
                }
            }

        }
    }
}

suspend fun getPost(image: String):String = suspendCancellableCoroutine{ continuation->
    var storage = Firebase.storage("gs://spaces-1751c.appspot.com")
    var storageRef = storage.reference
    var spaceRef = "aaa"
    storageRef.child(image).downloadUrl.addOnSuccessListener { uri->
        spaceRef = uri.toString()
        continuation.resume(spaceRef)
    }.addOnFailureListener {exception->
        continuation.resumeWithException(exception)
    }
}