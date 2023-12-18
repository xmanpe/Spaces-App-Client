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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture


data class posts (
    val document_id :String,
    val description : String,
    val usernameSpace : String,
    val usernameUser : String,
    val image : String
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FollowingScreen(navController: NavHostController, preferenceDatastore: PreferenceDatastore){
    var currUsername by remember { mutableStateOf("") }
//    var listFollowedPosts: MutableList<posts> = mutableListOf()
    var listFollowedPosts by remember { mutableStateOf(mutableListOf<posts>()) }
    val db = Firebase.firestore

    CoroutineScope(Dispatchers.IO).launch {
        preferenceDatastore.getSession().collect{
            withContext(Dispatchers.Main) {
                currUsername = it.username
            }
        }
    }
    Log.d("currUsername", currUsername)
    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.d("satu", "${document.id} => ${document.data}")
                listFollowedPosts.add(
                    posts(
                        document.id,
                        document.data["usernameSpace"].toString(),
                        document.data["usernameUser"].toString(),
                        document.data["description"].toString(),
                        document.data["image"].toString()

                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("HomeGetAllFollow", "Error getting documents.", exception)
        }
//    var tai = listFollowedPosts.distinctBy { it.document_id }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(255, 255, 255, 1))
        .verticalScroll(rememberScrollState())){
        TopAppBarWithSearch("Following")
        for(item in listFollowedPosts.distinctBy { it.document_id }){
            var imageRef by remember { mutableStateOf("") }

            LaunchedEffect(item) {
                // Perform asynchronous operation using coroutine
                imageRef = getPost(item.image).await()
            }
            ListFollowedSpace(item, imageRef)
        }
    }

}

@SuppressLint("CoroutineCreationDuringComposition")
suspend fun FollowingPosts(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {
    var listFollowedPosts: MutableList<posts> = mutableListOf()
    val db = Firebase.firestore
    //get the current user session
//    try {
    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
            Log.d("HomeGetAllFollow", "${document.id} => ${document.data}")
                listFollowedPosts.add(
                    posts(
                        document.id,
                        document.data?.get("name").toString(),
                        document.data?.get("username").toString(),
                        document.data?.get("description").toString(),
                        document.data?.get("image").toString()
                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("HomeGetAllFollow", "Error getting documents.", exception)
        }


}

@Composable
fun ListFollowedSpace(item: posts, image:String) {
    Log.d("image", item.toString())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(
                Color(0.9647f, 0.9647f, 0.9647f, 1f)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()

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
                        Text(text = "@"+ item.usernameSpace)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "1h")
                    }
                    Text(item.description)
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

fun getPost(image: String): CompletableFuture<String> {
    val storage = Firebase.storage("gs://spaces-1751c.appspot.com")
    val storageRef = storage.reference
    val future = CompletableFuture<String>()
    var spaceRef = ""

    storageRef.child(image).downloadUrl
        .addOnSuccessListener { uri ->
            spaceRef = uri.toString()
            Log.d("spaceRef2", spaceRef)
            future.complete(spaceRef)

        }
        .addOnFailureListener { exception ->
            future.completeExceptionally(exception)

        }
    return future
}
