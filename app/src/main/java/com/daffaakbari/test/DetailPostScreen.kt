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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController


@Preview(showBackground = true, name = "DetailPost Preview")
@Composable
fun PreviewDetailPost() {
    DetailPost(navController = rememberNavController()) // You need to provide a navController here, or modify your function to handle a null or mock controller.
}

@Composable
fun DetailPost(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBackBarWithSearch(navController)
        HeaderDetailPost()
        CommentsList(
            comments = listOf(
                CommentData(
                    username = "aldrik69",
                    text = "Gue suka sama celline sama rara!",
                    profilePictureId = R.drawable.house_01
                )
            )
        )
        Spacer(modifier = Modifier.weight(1f, true))
        CommentBox()
    }
}

@Composable
fun HeaderDetailPost() {
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
            Text(text = "@AWKOWOAAKW • ", style = MaterialTheme.typography.bodyMedium)
//            Text(text = "23m", style = MaterialTheme.typography.bodyMedium)
        }

        // Desc Post
        Text(text = "AOWWKOAKOAW",
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
                    onClick = {  },
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
                    Text(text = "0")
                }

                Button(
                    onClick = {  },
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
                    Text(text = "0")
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
fun CommentBox() {
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
            Text(text = "Add a comment...",
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
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
                    contentDescription = "Send comment"
                )
            }
        }

        // Hardcoded comment
//        Row(
//            verticalAlignment = Alignment.Top,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(4.dp)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.house_01), // Replace with actual image resource
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(30.dp)
//                    .clip(CircleShape)
//                    .background(Color.LightGray)
//            )
//            Spacer(Modifier.width(8.dp))
//            Column {
//                Text(text = "@username", style = MaterialTheme.typography.bodyMedium)
//                Text(text = "This is an example comment.",
//                    style = MaterialTheme.typography.bodySmall,
//                    modifier = Modifier.padding(end = 4.dp)
//                )
//            }
//        }
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

// Fake data
data class CommentData(val username: String, val text: String, val profilePictureId: Int)
