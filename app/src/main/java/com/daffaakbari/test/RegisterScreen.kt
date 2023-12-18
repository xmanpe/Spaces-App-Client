package com.daffaakbari.test

import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineStart
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Register(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    fun HandleRegister(username: String, name: String, email: String, password: String, confirmPassword: String) {
        if(username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return
        }

        if(password != confirmPassword) {
            return
        }

        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val db = Firebase.firestore

        val user = hashMapOf(
            "username" to username,
            "name" to name,
            "email" to email,
            "password" to hashedPassword
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("REGISTER", "DocumentSnapshot added with ID: ${documentReference.id}")
                navController.navigate("login") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("REGISTER", "Error adding document", e)
            }
    }

    fun NavigateToLogin() {
        navController.navigate("login") {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                cameraLauncher.launch(uri)
            }
            else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    fun UploadCapturedImage(uri: Uri, context: Context, username: String) {
        val storage = Firebase.storage
        var storageRef = storage.reference
        val fileName = username
        var spaceRef: StorageReference

        spaceRef = storageRef.child("users/$fileName.jpg")

        val byteArray: ByteArray? = context.contentResolver
            .openInputStream(uri)
            ?.use { it.readBytes() }

        Log.d("test byte", byteArray.toString())

        byteArray?.let {
            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()

            var uploadTask = spaceRef.putBytes(byteArray, metadata)
            uploadTask.addOnFailureListener {
                Log.d("UploadStorage", "Gagal")
            }.addOnSuccessListener {
                Log.d("UploadStorage", "Berhasil")
            }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Register", fontSize = 24.sp)

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
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if(capturedImageUri.path?.isNotEmpty() == true) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp).height(200.dp),
                painter = rememberImagePainter(capturedImageUri),
                contentDescription = null
            )
        }

        Button(
            onClick =
            {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)

                if(permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                }
                else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black),
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(width = 180.dp, height = 36.dp)
                .clip(CircleShape)
        ) {
            Text(text = "Take a photo")
        }

        Button(
            onClick =
            {
                HandleRegister(username, name, email, password, confirmPassword)
                UploadCapturedImage(uri, context, username)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black),
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(width = 180.dp, height = 36.dp)
                .clip(CircleShape)
        ) {
            Text(text = "Register")
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Already have an account? ", color = Color.Gray)
            ClickableText(
                text = AnnotatedString("Click here "),
                onClick = { offset ->
                    if (offset in 0..11) {
                        // Tambahkan logika navigasi ke halaman register di sini
                        NavigateToLogin()
                    }
                },
                style = TextStyle(
                    color = Color.Black, // Warna teks yang bisa diklik
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {"Click here" }
            )
            Text("to login", color = Color.Gray)
        }
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )

    return image
}