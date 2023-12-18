package com.daffaakbari.test

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.daffaakbari.test.session.PreferenceDatastore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CreateSpace(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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

    fun HandleCreateSpace(username: String, name: String, description: String, currUsername: String) {
        if(username.isEmpty() || name.isEmpty() || description.isEmpty() || currUsername.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        val user = hashMapOf(
            "username" to username,
            "name" to name,
            "description" to description,
            "usernameUser" to currUsername
        )

        db.collection("spaces")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("CREATESPACE", "DocumentSnapshot added with ID: ${documentReference.id}")
                navController.navigate("spaces") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("CREATESPACE", "Error adding document", e)
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Create Space", fontSize = 24.sp)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )

        Button(
            onClick = { HandleCreateSpace(username, name, description, currUsername) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black),
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .size(width = 180.dp, height = 36.dp)
                .clip(CircleShape)
        ) {
            Text(text = "Create Space")
        }
    }
}