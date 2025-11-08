package com.example.examenfinal

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import model.Routes


@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var carnet by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // 🔹 Textos del archivo strings.xml
    val cdIconRegister = stringResource(R.string.cd_icon_register)
    val registerTitle = stringResource(R.string.register_title)
    val labelNombre = stringResource(R.string.label_nombre)
    val labelEmail = stringResource(R.string.label_email)
    val labelPassword = stringResource(R.string.label_password)
    val labelCarrera = stringResource(R.string.label_carrera)
    val registerButton = stringResource(R.string.register_button)
    val registerSuccess = stringResource(R.string.register_success)
    val registerFail = stringResource(R.string.register_fail)
    val backToLogin = stringResource(R.string.back_to_login)
    val validationError = stringResource(R.string.validation_errorr)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = cdIconRegister,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFF0066B3)
        )

        Text(
            text = registerTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0066B3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre completo
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(labelNombre) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Carné universitario
        OutlinedTextField(
            value = carnet,
            onValueChange = { carnet = it },
            label = { Text("Carné universitario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Carrera
        OutlinedTextField(
            value = carrera,
            onValueChange = { carrera = it },
            label = { Text(labelCarrera) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Foto (URL)
        OutlinedTextField(
            value = fotoUrl,
            onValueChange = { fotoUrl = it },
            label = { Text("URL de la foto") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(labelEmail) },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            supportingText = {
                if (emailError.isNotEmpty()) Text(emailError, color = Color.Red)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(labelPassword) },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            supportingText = {
                if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red)
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Botón registrar
        Button(
            onClick = {
                val emailValid = validateEmail(email, context)
                val passValid = validatePassword(password, context)

                emailError = emailValid.second
                passwordError = passValid.second

                if (emailValid.first && passValid.first &&
                    nombre.isNotBlank() && carrera.isNotBlank() && carnet.isNotBlank()
                ) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                                val userData = hashMapOf(
                                    "nombre" to nombre,
                                    "email" to email,
                                    "carrera" to carrera,
                                    "carnet" to carnet,
                                    "foto" to fotoUrl,
                                    "rol" to "estudiante"
                                )
                                db.collection("usuarios").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, registerSuccess, Toast.LENGTH_SHORT).show()
                                        navController.navigate(Routes.Login.route)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, registerFail, Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, registerFail, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, validationError, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0066B3),
                contentColor = Color.White
            )
        ) {
            Text(registerButton)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navController.navigate(Routes.Login.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(backToLogin)
        }
    }
}
