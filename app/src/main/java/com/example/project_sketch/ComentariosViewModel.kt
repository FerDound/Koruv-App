package com.example.project_sketch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Comentario(
    val id: String = "",
    val texto: String = "",
    val autorId: String = "",
    val autorNombre: String = "",
    val autorUsuario: String = "",
    val autorFoto: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
)

class ComentariosViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var publicando by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var publicadoExitoso by mutableStateOf(false)
        private set

    var comentarios by mutableStateOf<List<Comentario>>(emptyList())
        private set

    private var listenerComentarios: ListenerRegistration? = null

    fun escucharComentarios(postId: String) {
        comentarios = emptyList()
        listenerComentarios?.remove()
        listenerComentarios = db.collection("posts").document(postId)
            .collection("comentarios")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                comentarios = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Comentario::class.java)?.copy(id = doc.id)
                }
            }
    }
    override fun onCleared() {
        listenerComentarios?.remove()
    }

    fun hacerComentario(postId: String, texto: String) {
        if (texto.isBlank()) return
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            publicando = true
            try {
                val userDoc = db.collection("users").document(userId).get().await()
                val comentario = Comentario(
                    texto = texto.trim(),
                    autorId = userId,
                    autorNombre = userDoc.getString("nombrepublico") ?: "",
                    autorUsuario = userDoc.getString("usuario") ?: "",
                    autorFoto = userDoc.getString("avatarUrl") ?: "",
                )
                db.collection("posts").document(postId)
                    .collection("comentarios").add(comentario).await()
                db.collection("posts").document(postId)
                    .update("comentarios", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()
                publicadoExitoso = true
            } catch (e: Exception) {
                errorMessage = e.message
            }
            publicando = false
        }
    }

    fun darLikeComentario(
        postId: String,
        comentarioId: String
    ) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val ref = db.collection("posts")
                    .document(postId)
                    .collection("comentarios")
                    .document(comentarioId)

                val doc = ref.get().await()

                val likedBy =
                    doc.get("likedBy") as? List<String> ?: emptyList()

                if (userId in likedBy) {

                    ref.update(
                        mapOf(
                            "likedBy" to (likedBy - userId),
                            "likes" to ((doc.getLong("likes") ?: 1) - 1)
                        )
                    ).await()

                } else {

                    ref.update(
                        mapOf(
                            "likedBy" to (likedBy + userId),
                            "likes" to ((doc.getLong("likes") ?: 0) + 1)
                        )
                    ).await()
                }

            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun eliminarComentario(postId: String, comentarioId: String) {
        viewModelScope.launch {
            try {
                db.collection("posts").document(postId)
                    .collection("comentarios").document(comentarioId)
                    .delete().await()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}