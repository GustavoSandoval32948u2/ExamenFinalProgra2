package com.example.examenfinal


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var prestamos by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    val title = stringResource(R.string.admin_panel_title)
    val approve = stringResource(R.string.admin_approve)
    val reject = stringResource(R.string.admin_reject)
    val noRequests = stringResource(R.string.admin_no_requests)

    // 🔹 Cargar solicitudes en tiempo real
    LaunchedEffect(Unit) {
        db.collection("prestamos").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                prestamos = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    data + mapOf("id" to doc.id)
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (prestamos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(noRequests)
                }
            } else {
                LazyColumn {
                    items(prestamos) { prestamo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Equipo: ${prestamo["equipo"] ?: "Desconocido"}")
                                Text("Usuario: ${prestamo["usuario"] ?: "Sin nombre"}")
                                Text("Estado: ${prestamo["estado"] ?: "Pendiente"}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            prestamo["id"]?.let {
                                                db.collection("prestamos").document(it.toString())
                                                    .update("estado", "Aprobado")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White
                                        )
                                    ) { Text(approve) }

                                    Button(
                                        onClick = {
                                            prestamo["id"]?.let {
                                                db.collection("prestamos").document(it.toString())
                                                    .update("estado", "Rechazado")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFC62828),
                                            contentColor = Color.White
                                        )
                                    ) { Text(reject) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
