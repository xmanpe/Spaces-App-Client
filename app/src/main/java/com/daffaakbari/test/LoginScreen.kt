package com.daffaakbari.test

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import com.daffaakbari.test.session.PreferenceDatastore
import com.daffaakbari.test.session.SessionModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Login(navController: NavHostController, preferenceDatastore: PreferenceDatastore) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun HandleLogin(username: String, password: String) {
        if(username.isEmpty() || password.isEmpty()) {
            return
        }

        val db = Firebase.firestore

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("LOGIN", "${document.id} => ${document.data}")
                    // Check Username dan Password
                    if(document.data["username"].toString() == username && document.data["password"].toString() == password) {
                        // Set username and email User to Session
                        CoroutineScope(Dispatchers.IO).launch {
                            var modelSession = SessionModel(document.data["username"].toString(), document.data["email"].toString())
                            preferenceDatastore.setSession(modelSession)
                        }
                        // Navigate to page Home
                        navController.navigate("home") {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("LOGIN", "Error getting documents.", exception)
            }
    }

    fun NavigateToRegister() {
        navController.navigate("register") {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Login", fontSize = 24.sp)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = { HandleLogin(username, password) },
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
            Text(text = "Login")
        }

        Row {
            Text(text = "Didn't have account?")
            Button(
                onClick = { NavigateToRegister() },
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
                Text(text = "Register")
            }
        }
    }
}