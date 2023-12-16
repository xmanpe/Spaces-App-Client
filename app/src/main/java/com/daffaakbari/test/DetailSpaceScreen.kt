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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffaakbari.test.session.PreferenceDatastore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
data class SpaceFollowingDetail(
    val document: String,
    val spaceUsername: String,
    val followUsername: String
)

data class PostDetail(
    val idPost: String,
    val usernameSpace: String,
    val usernameUser: String,
    val description: String,
    val totalLike: String,
    val totalDislike: String
)

data class ListPostLike(
    val idPost: String,
    val usernameUser: String
)

data class ListPostDislike(
    val idPost: String,
    val usernameUser: String
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailSpace(navController: NavHostController, preferenceDatastore: PreferenceDatastore, usernameSpace: String) {
    var spaceName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var documentFollow by remember { mutableStateOf("") }
    var isOwner by remember { mutableStateOf(false) }
    var isFollow by remember { mutableStateOf(false) }
    var totalFollower = 0
    var totalPost = 0

    // For Dialog
    val openDialogCreatePost = remember { mutableStateOf(false) }

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

    // Get space data
    val db = Firebase.firestore
    db.collection("spaces")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("DetailSpace", "${document.id} => ${document.data}")
                if(document.data["username"].toString() == usernameSpace) {
                    spaceName = document.data["name"].toString()
                    description = document.data["description"].toString()
                    if(document.data["usernameUser"].toString() == currUsername) {
                        isOwner = true
                    }
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("DetailSpace", "Error getting documents.", exception)
        }

    // Get all follow
    var listFollow by remember { mutableStateOf(mutableListOf<SpaceFollowingDetail>()) }
    db.collection("follow")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("HomeGetAllFollow", "${document.id} => ${document.data}")
                listFollow.add(
                    SpaceFollowingDetail(
                        document.id,
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

    // Get total like
    var listLike by remember { mutableStateOf(mutableListOf<ListPostLike>()) }
    db.collection("postslike")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("PostLike", "${document.id} => ${document.data}")
                listLike.add(
                    ListPostLike(
                        idPost = document.data["idPost"].toString(),
                        usernameUser = document.data["usernameUser"].toString()
                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("PostLike", "Error getting documents.", exception)
        }
    // Remove duplicate data
    val distinctListLike = listLike.distinctBy { it.usernameUser }

    // Get total dislike
    var listDislike by remember { mutableStateOf(mutableListOf<ListPostDislike>()) }
    db.collection("postsdislike")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("PostLike", "${document.id} => ${document.data}")
                listDislike.add(
                    ListPostDislike(
                        idPost = document.data["idPost"].toString(),
                        usernameUser = document.data["usernameUser"].toString()
                    )
                )
            }
        }
        .addOnFailureListener { exception ->
            Log.w("PostLike", "Error getting documents.", exception)
        }
    // Remove duplicate data
    val distinctListDislike = listDislike.distinctBy { it.usernameUser }

    // Get all post
    var listPost by remember { mutableStateOf(mutableListOf<PostDetail>()) }

    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("DetailGetPost", "${document.id} => ${document.data}")
                var totalLike = 0
                var totalDislike = 0
                if(document.data["usernameSpace"].toString() == usernameSpace) {
                    // Get total like on a post
                    for(item in distinctListLike) {
                        if(item.idPost == document.id) {
                            totalLike++
                        }
                    }

                    // Get total dislike on a post
                    for(item in distinctListDislike) {
                        if(item.idPost == document.id) {
                            totalDislike++
                        }
                    }

                    listPost.add(
                        PostDetail(
                            document.id,
                            document.data["usernameSpace"].toString(),
                            document.data["usernameUser"].toString(),
                            document.data["description"].toString(),
                            totalLike.toString(),
                            totalDislike.toString()
                        )
                    )
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("DetailGetPost", "Error getting documents.", exception)
        }
    // Remvoe duplicate post
    val distinctListPost = listPost.distinctBy { it.description }

    for(item in distinctlistFollow) {
        // Count follower
        if(item.spaceUsername == usernameSpace) {
            totalFollower++
        }
        // Set is space follow
        if(item.followUsername == currUsername && item.spaceUsername == usernameSpace) {
            isFollow = true
            documentFollow = item.document
        }
    }

    // Get total post in a space
    for(item in distinctListPost) {
        if(item.usernameSpace == usernameSpace) {
            totalPost++
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBackBarWithSearch(navController)
        HeaderDetailSpace(
            spaceName,
            usernameSpace,
            description,
            totalFollower.toString(),
            totalPost.toString(),
            currUsername,
            documentFollow,
            isFollow,
            isOwner
        )
        LazyColumn {
            items(count = distinctListPost.size) { index ->
                PostWithoutImage(
                    idPost = distinctListPost[index].idPost,
                    usernameUser = distinctListPost[index].usernameUser,
                    description = distinctListPost[index].description,
                    currUsername = currUsername,
                    totalLike = distinctListPost[index].totalLike,
                    totalDislike = distinctListPost[index].totalDislike,
                    navController = navController
                )
            }
        }
    }

    // Fab Button
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = { openDialogCreatePost.value = !openDialogCreatePost.value },
            shape = CircleShape,
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }

    // Dialog atau modal for create post
    when {
        openDialogCreatePost.value -> {
            DialogCreatePost(
                onDismissRequest = { openDialogCreatePost.value = false },
                onConfirmation = { openDialogCreatePost.value = false },
                usernameSpace,
                currUsername
            )
        }
    }
}

@Composable
fun HeaderDetailSpace(
    spaceName: String,
    usernameSpace: String,
    description: String,
    totalFollwer: String,
    totalPost: String,
    currUsername: String,
    documentFollow: String,
    isFollow: Boolean,
    isOwner: Boolean
) {
    fun HandleFollow(username: String, currUsername: String) {
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

    fun HandleUnFollow(documentFollow: String) {
        if(documentFollow.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        db.collection("follow").document(documentFollow)
            .delete()
            .addOnSuccessListener { Log.d("Detail", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Detail", "Error deleting document", e) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                    Text(text = totalPost, fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Posts", fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(32.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = totalFollwer, fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                    Text(text = "People", fontSize = 14.sp, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        // Nama dan desc space
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = spaceName, fontSize = 24.sp, style = MaterialTheme.typography.titleMedium)
            Text(text = "@$usernameSpace", style = MaterialTheme.typography.bodyMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
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
                onClick =
                {
                    if(isFollow) {
                        HandleUnFollow(documentFollow)
                    } else if(isFollow == false) {
                        HandleFollow(usernameSpace, currUsername)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(isFollow) Color.White else Color.Black,
                    contentColor = if(isFollow) Color.Black else Color.White
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = CircleShape,
                modifier = Modifier
                    .size(width = 180.dp, height = 36.dp)
                    .clip(CircleShape)
            ) {
                if(isOwner) {
                    Text(text = "Owned")
                }
                else if(isFollow == true) {
                    Text(text = "Following")
                }
                else if(isFollow == false) {
                    Text(text = "Follow")
                }
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

@Composable
fun PostWithoutImage(
    idPost: String,
    usernameUser: String,
    description: String,
    currUsername: String,
    totalLike: String,
    totalDislike: String,
    navController: NavHostController
) {
    fun NavigateToDetailPost() {
        navController.navigate("detailpost")
    }

    fun HandleLike(idPost: String, currUsername: String) {
        if(idPost.isEmpty() || currUsername.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        val like = hashMapOf(
            "idPost" to idPost,
            "usernameUser" to currUsername
        )

        db.collection("postslike")
            .add(like)
            .addOnSuccessListener { documentReference ->
//                Log.d("CreatePost", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
//                Log.w("CreatePost", "Error adding document", e)
            }
    }

    fun HandleDislike(idPost: String, currUsername: String) {
        if(idPost.isEmpty() || currUsername.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        val dislike = hashMapOf(
            "idPost" to idPost,
            "usernameUser" to currUsername
        )

        db.collection("postsdislike")
            .add(dislike)
            .addOnSuccessListener { documentReference ->
//                Log.d("CreatePost", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
//                Log.w("CreatePost", "Error adding document", e)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .border(2.dp, Color.Transparent, RoundedCornerShape(10.dp))
            .clickable { NavigateToDetailPost() }
            .padding(8.dp)
    ) {
        // User post
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.house_01), // Replace with actual image resource
                contentDescription = "Space Image",
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "@$usernameUser • ", style = MaterialTheme.typography.bodyMedium)
//            Text(text = "23m", style = MaterialTheme.typography.bodyMedium)
        }

        // Desc Post
        Text(text = description,
             style = MaterialTheme.typography.bodyMedium,
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(4.dp)
        )

        // Tindakan post
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row {
                Button(
                    onClick = { HandleLike(idPost, currUsername) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = CircleShape,
                    modifier = Modifier
                        .clip(CircleShape)
                ) {
                    Icon(
//                        if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        Icons.Rounded.FavoriteBorder,
                        contentDescription = "Jumlah Like"
                    )
                    Text(text = totalLike)
                }

                Button(
                    onClick = { HandleDislike(idPost, currUsername) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = CircleShape,
                    modifier = Modifier
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Jumlah Dislike"
                    )
                    Text(text = totalDislike)
                }
            }
            Row {
                Button(
                    onClick = { /* Handle follow action */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = CircleShape,
                    modifier = Modifier
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.Rounded.Send,
                        contentDescription = "Jumlah Comment"
                    )
                    Text(text = " | Comments")
                }
            }
        }
    }
}

@Composable
fun DialogCreatePost(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    usernameSpace: String,
    usernameUser: String
) {
    var description by remember { mutableStateOf("") }

    fun HandleCreatePost(usernameSpace: String, usernameUser: String, description: String) {
        if(usernameSpace.isEmpty() || usernameUser.isEmpty() || description.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        val post = hashMapOf(
            "usernameSpace" to usernameSpace,
            "usernameUser" to usernameUser,
            "description" to description
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { documentReference ->
//                Log.d("CreatePost", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
//                Log.w("CreatePost", "Error adding document", e)
            }
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Post for @$usernameSpace"
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Button(
                    onClick =
                    {
                        HandleCreatePost(usernameSpace, usernameUser, description)
                        onConfirmation()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 120.dp, height = 36.dp)
                        .clip(CircleShape)
                ) {
                    Text(text = "Post")
                }
            }
        }
    }
}