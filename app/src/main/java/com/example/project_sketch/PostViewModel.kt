package com.example.project_sketch

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Post(
    val id: String = "",
    val texto: String = "",
    val autorId: String = "",
    val autorNombre: String = "",
    val autorUsuario: String = "",
    val autorFoto: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val comentarios: Int = 0,
    val likedBy: List<String> = emptyList(),
    val imagenUrl: String? = null,
)

class PublicarViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var publicando by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var publicadoExitoso by mutableStateOf(false)
        private set

    fun publicar(texto: String, imagenUri: Uri? = null, contexto: Context? = null) {
        if (texto.isBlank() && imagenUri == null) {
            errorMessage = "Escribe algo o agrega una imagen"
            return
        }
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch { publicando = true; errorMessage = null
            try {
                val userDoc = db.collection("users").document(userId).get().await()
                val nombrePublico = userDoc.getString("nombrepublico") ?: ""
                val usuario = userDoc.getString("usuario") ?: ""
                val imageUrl = if (imagenUri != null && contexto != null) {
                    subirACloudinary(contexto, imagenUri) ?: run {
                        errorMessage = "Error subiendo imagen"
                        publicando = false
                        return@launch
                    }
                } else null
                val avatarUrl = userDoc.getString("avatarUrl") ?: ""

                val post = Post(
                    texto = texto.trim(),
                    autorId = userId,
                    autorNombre = nombrePublico,
                    autorUsuario = usuario,
                    autorFoto = avatarUrl,
                    imagenUrl = imageUrl
                )
                db.collection("posts").add(post).await()
                publicadoExitoso = true
            } catch (e: Exception) {
                errorMessage = e.message
            }
            publicando = false
        }
    }

    fun editar(
        postId: String,
        textoNuevo: String,
        imagenUri: Uri? = null,
        imagenUrlActual: String? = null,
        quitarImagen: Boolean = false,
        contexto: Context? = null
    ) {
        if (textoNuevo.isBlank() && imagenUri == null && quitarImagen) {
            errorMessage = "Escribe algo antes de publicar"
            return
        }
        viewModelScope.launch {
            publicando = true
            try {
                val updates = mutableMapOf<String, Any?>()
                updates["texto"] = textoNuevo

                when {
                    imagenUri != null && contexto != null -> {
                        val url = subirACloudinary(contexto, imagenUri)
                        if (url != null) updates["imagenUrl"] = url
                        else {
                            errorMessage = "Error subiendo imagen"
                            publicando = false
                            return@launch
                        }
                    }
                    quitarImagen -> updates["imagenUrl"] = null
                }
                db.collection("posts").document(postId)
                    .set(updates, SetOptions.merge())
                    .await()
                publicadoExitoso = true
            } catch (e: Exception) {
                errorMessage = e.message
            }
            publicando = false
        }
    }

    fun resetEstado() {
        publicadoExitoso = false
        errorMessage = null
    }

}