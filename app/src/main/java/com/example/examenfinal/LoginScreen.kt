package com.example.examenfinal


import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import model.Routes


@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val activity = LocalView.current.context as Activity

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = stringResource(R.string.cd_icon_person),
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF0066B3)
        )

        Text(
            text = stringResource(R.string.login_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0066B3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.label_email)) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            supportingText = {
                if (emailError.isNotEmpty()) Text(emailError, color = Color.Red)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.label_password)) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            supportingText = {
                if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red)
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val emailValid = validateEmail(email, context)
                val passValid = validatePassword(password, context)

                emailError = emailValid.second
                passwordError = passValid.second

                if (emailValid.first && passValid.first) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                                val userDoc = Firebase.firestore.collection("usuarios").document(userId)

                                userDoc.get().addOnSuccessListener { doc ->
                                    if (!doc.exists()) {
                                        // Crear documento si no existe
                                        userDoc.set(
                                            hashMapOf(
                                                "correo" to email,
                                                "nombre" to "Administrador",
                                                "rol" to "admin"
                                            )
                                        )
                                    }

                                    val rol = doc.getString("rol") ?: "estudiante"
                                    if (rol == "admin") {
                                        navController.navigate(Routes.AdminPanel.route) {
                                            popUpTo(Routes.Login.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(Routes.StudentHome.route) {
                                            popUpTo(Routes.Login.route) { inclusive = true }
                                        }
                                    }
                                }
                            } else {
                                message = context.getString(R.string.login_error)
                            }
                        }
                } else {
                    Toast.makeText(context, context.getString(R.string.validation_error), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0066B3),
                contentColor = Color.White
            )
        ) {
            Text(stringResource(R.string.login_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.navigate(Routes.Register.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.register_button))
        }

    }
}
