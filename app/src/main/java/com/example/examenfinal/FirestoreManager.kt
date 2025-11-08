package com.example.examenfinal


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 🔹 Obtener todos los equipos
    suspend fun getEquipos(): List<Map<String, Any>> {
        return try {
            val result = db.collection("equipos").get().await()
            result.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                data + mapOf("id" to doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 Crear solicitud de préstamo
    suspend fun solicitarPrestamo(equipoNombre: String) {
        val user = auth.currentUser ?: return
        val fechaPrestamo = java.util.Calendar.getInstance()
        val fechaDevolucion = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, 3)
        }

        val prestamo = hashMapOf(
            "usuario" to user.email,
            "equipo" to equipoNombre,
            "estado" to "Pendiente",
            "fechaPrestamo" to fechaPrestamo.time.toString(),
            "fechaDevolucion" to fechaDevolucion.time.toString()
        )

        db.collection("prestamos").add(prestamo).await()
    }

    // 🔹 Obtener préstamos por usuario actual
    suspend fun getPrestamosUsuario(): List<Map<String, Any>> {
        val user = auth.currentUser ?: return emptyList()
        return try {
            val result = db.collection("prestamos")
                .whereEqualTo("usuario", user.email)
                .get()
                .await()
            result.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 Cambiar estado de préstamo (para admin)
    suspend fun actualizarEstadoPrestamo(prestamoId: String, nuevoEstado: String) {
        db.collection("prestamos").document(prestamoId)
            .update("estado", nuevoEstado)
            .await()
    }

    // 🔹 Obtener préstamos pendientes (para admin)
    suspend fun getPrestamosPendientes(): List<Map<String, Any>> {
        return try {
            val result = db.collection("prestamos")
                .whereEqualTo("estado", "Pendiente")
                .get()
                .await()
            result.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList()
        }
    }
}