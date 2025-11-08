package com.example.examenfinal


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var equipos by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    val title = stringResource(R.string.student_home_title)
    val requestBtn = stringResource(R.string.student_home_request)
    val available = stringResource(R.string.student_home_available)
    val notAvailable = stringResource(R.string.student_home_not_available)
    val noEquipments = stringResource(R.string.student_home_no_equipment)
    val requestSent = stringResource(R.string.student_home_request_sent)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🔹 Cargar equipos disponibles
    LaunchedEffect(Unit) {
        db.collection("equipos")
            .get()
            .addOnSuccessListener { result ->
                equipos = result.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    data + mapOf("id" to doc.id)
                }
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (equipos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(noEquipments)
                }
            } else {
                LazyColumn {
                    items(equipos) { equipo ->
                        val disponible = equipo["disponible"] as? Boolean ?: false
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F7FF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val nombre = equipo["nombre"] ?: "Equipo"
                                val descripcion = equipo["descripcion"] ?: ""
                                val imagen = equipo["imagen"] ?: ""

                                if (imagen.toString().isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imagen),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Text(text = nombre.toString(), style = MaterialTheme.typography.titleMedium)
                                Text(text = descripcion.toString(), style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (disponible) available else notAvailable,
                                    color = if (disponible) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (disponible) {
                                    Button(
                                        onClick = {
                                            val user = auth.currentUser ?: return@Button
                                            val fechaPrestamo = Calendar.getInstance()
                                            val fechaDevolucion = Calendar.getInstance().apply {
                                                add(Calendar.DAY_OF_YEAR, 3)
                                            }

                                            val prestamo = hashMapOf(
                                                "usuario" to user.email,
                                                "equipo" to nombre,
                                                "estado" to "Pendiente",
                                                "fechaPrestamo" to fechaPrestamo.time.toString(),
                                                "fechaDevolucion" to fechaDevolucion.time.toString()
                                            )

                                            db.collection("prestamos").add(prestamo)
                                                .addOnSuccessListener {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(requestSent)
                                                    }
                                                }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF0066B3),
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(requestBtn)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
