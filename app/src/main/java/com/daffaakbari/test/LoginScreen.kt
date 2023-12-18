package com.daffaakbari.test

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
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
import org.mindrot.jbcrypt.BCrypt
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

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
//                    Log.d("LOGIN", "${document.id} => ${document.data}")

                    // Check Username dan Password
                    if(document.data["username"].toString() == username && BCrypt.checkpw(password, document.data["password"].toString())) {
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
        Image(
            painter = painterResource(id = R.drawable.chat_conversation_circle),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Let's talk things!",
            fontSize = 19.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(5.dp)) // Tambahkan space antara teks pertama dan kedua
        Text(
            text = "Join us talk about things you are interested in.",
            fontSize = 10.sp,
            color = Color.Gray
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            shape = RoundedCornerShape(30.dp), // Ganti 20.dp dengan lebar yang sesuai
            modifier = Modifier
                .fillMaxWidth(5f)
                .padding(5.dp)
                .aspectRatio(6f)
                .widthIn(min = 50.dp, max = 100.dp)
                .height(50.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(30.dp), // Ganti 20.dp dengan lebar yang sesuai
            modifier = Modifier
                .fillMaxWidth(5f)
                .padding(5.dp)
                .aspectRatio(6f)
                .widthIn(min = 50.dp, max = 100.dp)
                .height(50.dp)
        )
        ElevatedButton(
            onClick = { HandleLogin(username, password) },
            contentPadding = PaddingValues(9.dp),
            modifier = Modifier.defaultMinSize(
                minWidth = 360.dp,
                minHeight = 30.dp
            ),

            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
        ) {
            Text(
                text = "Log In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add space of 16dp

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Haven't made an account yet? ", color = Color.Gray)
            ClickableText(
                text = AnnotatedString("Click here "),
                onClick = { offset ->
                    if (offset in 0..11) {
                        // Tambahkan logika navigasi ke halaman register di sini
                        NavigateToRegister()
                    }
                },
                style = TextStyle(
                    color = Color.Black, // Warna teks yang bisa diklik
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {"Click here" }
            )
            Text("to register", color = Color.Gray)
        }
    }
}