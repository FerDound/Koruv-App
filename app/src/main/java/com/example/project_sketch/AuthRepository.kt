package com.example.project_sketch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        username: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        noControl: String,
        nombrepublico: String,
        carrera: String,
        semestre: String
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            val userId = result.user?.uid ?: throw Exception("Error al obtener usuario")

            val user = hashMapOf(
                "uid" to userId,
                "usuario" to username,
                "nombre" to nombre,
                "apellidoPaterno" to apellidoPaterno,
                "apellidoMaterno" to apellidoMaterno,
                "nombrepublico" to nombrepublico,
                "avatarUrl" to "",
                "carrera" to carrera,
                "semestre" to semestre,
                "numeroControl" to noControl,
                "followersCount" to 0,
                "followingCount" to 0,
                "postsCount" to 0,
                "createdAt" to System.currentTimeMillis(),
                "siguiendoList" to emptyList<String>(),
                "seguidoresList" to emptyList<String>()
            )

            db.collection("users").document(userId).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}