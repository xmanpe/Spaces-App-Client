package com.daffaakbari.test

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


data class postsAsal (
    val description : String,
    val usernameSpace : String,
    val usernameUser : String,
)
@Composable
fun FollowingScreen() {
    var listFollowedUser by remember { mutableStateOf(mutableListOf<postsAsal>()) }
    val db = Firebase.firestore
    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.d("SPACES", "${document.id} => ${document.data}")
                // Check if currUser have a space
//                if(document.data["usernameUser"].toString() == currUsername) {
//                    listOwnedSpace.add(
//                        OwnedSpaceItem(
//                            document.data["name"].toString(),
//                            document.data["username"].toString(),
//                            document.data["description"].toString(),
//                            navController
//                        )
//                    )
//                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("SPACES", "Error getting documents.", exception)
        }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarWithSearch("Following")
        ListFollowedSpace()
    }
}

@Composable
fun ListPosts() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(count = 5) { index ->

        }
    }
}



@Composable
fun ListFollowedSpace() {
    LazyColumn() {
        items(count = 5) { index ->
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
                            painter = painterResource(id = R.drawable.house_01),
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
                            Image(painter = painterResource(
                                id = R.drawable.house_01),
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
//            Image(
//                painter = painterResource(id = R.drawable.house_01),
//                contentDescription = "Space Image",
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .background(Color.LightGray)
//            )
        }
    }
}