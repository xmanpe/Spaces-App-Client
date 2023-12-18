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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.daffaakbari.test.session.PreferenceDatastore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//@Preview(showBackground = true, name = "DetailPost Preview")
//@Composable
//fun PreviewDetailPost() {
//    DetailPost(navController = rememberNavController()) // You need to provide a navController here, or modify your function to handle a null or mock controller.
//}


data class CommentData(val username: String, val text: String, val profilePictureId: Int)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailPost(navController: NavHostController, preferenceDatastore: PreferenceDatastore, documentedId: String) {
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var totalLike = 0
    var totalDislike = 0
    var totalComment = 0

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

    // Get all post
    val db = Firebase.firestore

    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("DetailGetPost", "${document.id} => ${document.data}")
                if(document.id == documentedId) {
                    username = document.data["usernameUser"].toString()
                    description = document.data["description"].toString()
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("DetailGetPost", "Error getting documents.", exception)
        }

    // Get all comment
    var listComment by remember { mutableStateOf(mutableListOf<CommentData>()) }
    db.collection("comments")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
//                Log.d("PostLike", "${document.id} => ${document.data}")
                if(document.data["idPost"].toString() == documentedId) {
                    listComment.add(
                        CommentData(
                            username = document.data["usernameUser"].toString(),
                            text = document.data["comment"].toString(),
                            profilePictureId = R.drawable.house_01
                        )
                    )
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("PostLike", "Error getting documents.", exception)
        }
    // Remove duplicate data
    val distinctListComment = listComment.distinctBy { it.text }

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

    for(item in distinctListLike) {
        if(item.idPost == documentedId) {
            totalLike++
        }
    }

    for(item in distinctListDislike) {
        if(item.idPost == documentedId) {
            totalDislike++
        }
    }

    for(item in distinctListComment) {
        totalComment++
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBackBarWithSearch(navController)
        HeaderDetailPost(documentedId, currUsername, username, description, totalLike.toString(), totalDislike.toString(), totalComment.toString())
        CommentsList(
            comments = distinctListComment
        )
        Spacer(modifier = Modifier.weight(1f, true))
        CommentBox(documentedId, currUsername)
    }
}

@Composable
fun HeaderDetailPost(
    idPost: String,
    currUsername: String,
    username: String,
    description: String,
    totalLike: String,
    totalDislike: String,
    totalComment: String
) {
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
            Text(text = "@$username • ", style = MaterialTheme.typography.bodyMedium)
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
                    Text(text = " | $totalComment Comments")
                }
            }
        }
    }
}

@Composable
fun CommentBox(documentId: String, currUsername: String) {
    var comment by remember { mutableStateOf("") }

    fun HandleCreateComment(idPost: String, usernameUser: String, comment: String) {
        if(idPost.isEmpty() || usernameUser.isEmpty() || comment.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        val post = hashMapOf(
            "idPost" to idPost,
            "usernameUser" to usernameUser,
            "comment" to comment
        )

        db.collection("comments")
            .add(post)
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
            .padding(8.dp)
    ) {
        // Comment input field
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Add a comment") }
            )
            Button(
                onClick = { HandleCreateComment(documentId, currUsername, comment) },
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
                    contentDescription = "Send comment"
                )
            }
        }
    }
}

// Comment item
@Composable
fun CommentItem(username: String, commentText: String, profilePictureId: Int) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = profilePictureId),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(text = "@$username", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = commentText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@Composable
fun CommentsList(comments: List<CommentData>) {
    LazyColumn {
        items(comments) { comment ->
            CommentItem(
                username = comment.username,
                commentText = comment.text,
                profilePictureId = comment.profilePictureId
            )
        }
    }
}

