package com.example.project_sketch

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PerfilViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Datos del perfil
    var fotoPerfil by mutableStateOf<String?>(null)
        private set
    var nombrepublico by mutableStateOf("")
        private set
    var usuario by mutableStateOf("")
        private set
    var carrera by mutableStateOf("")
        private set
    var semestre by mutableStateOf("")
        private set
    var cantidadPosts by mutableIntStateOf(0)
        private set
    var seguidores by mutableIntStateOf(0)
        private set
    var siguiendo by mutableIntStateOf(0)
        private set
    private var siguiendoListCache = emptyList<String>()

    var sigueAlUsuario by mutableStateOf(false)
        private set

    var procesandoSeguir by mutableStateOf(false)
        private set

    // Galería
    var postsImagenes by mutableStateOf<List<Post>>(emptyList())
        private set
    var seleccionados by mutableStateOf(setOf<Post>())
        private set

    var imagenAbierta by mutableStateOf<Post?>(null)

    // Posts
    var postsNormales by mutableStateOf<List<Post>>(emptyList())
        private set


    var usuariosFeed by mutableStateOf<List<Map<String, String>>>(emptyList())
        private set


    // Estado de carga
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set


    fun cargarPerfil(userId: String? = null) {
        val uid = userId ?: auth.currentUser?.uid ?: return
        perfilActualId = uid
        viewModelScope.launch {
            isLoading = true
            try {
                val doc = db.collection("users").document(uid).get().await()
                val miUid = auth.currentUser?.uid
                if (miUid != null && miUid != uid) {
                    val miDoc = db.collection("users").document(miUid).get().await()
                    siguiendoListCache = (miDoc.get("siguiendoList") as? List<*>)
                        ?.mapNotNull { it as? String } ?: emptyList()
                    sigueAlUsuario = uid in siguiendoListCache
                }
                nombrepublico = doc.getString("nombrepublico") ?: "Usuario"
                usuario = doc.getString("usuario") ?: "usuario"
                seguidores = doc.getLong("followersCount")?.toInt() ?: 0
                siguiendo = doc.getLong("followingCount")?.toInt() ?: 0
                fotoPerfil = doc.getString("avatarUrl")
                val carreraDoc = doc.getString("carrera") ?: ""
                val semestreDoc = doc.getString("semestre") ?: ""
                carrera = carreraDoc
                semestre =
                    if (carreraDoc.isNotEmpty() && semestreDoc.isNotEmpty()) {
                        val inicialCarrera = when (carreraDoc) {
                            "Alimentos"       -> "A"
                            "Ciberseguridad"  -> "S"
                            "Contabilidad"    -> "C"
                            "Electrónica"     -> "E"
                            "Laboratorista"   -> "L"
                            "Mantenimiento"   -> "M"
                            "Programación"    -> "P"
                            else -> ""
                        }
                        "$semestreDoc$inicialCarrera"
                    } else semestreDoc
                cargarPosts(uid)
            } catch (e: Exception) {
                errorMessage = e.message
            }
            isLoading = false
        }
    }

    private suspend fun cargarPosts(userId: String) {
        try {
            val snapshot = db.collection("posts")
                .whereEqualTo("autorId", userId)
                .get()
                .await()
            val todos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)?.copy(id = doc.id)
            }.sortedByDescending { it.timestamp }
            postsNormales = todos
            postsImagenes = todos.filter { !it.imagenUrl.isNullOrEmpty() }
            cantidadPosts = todos.size
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    private var perfilActualId: String? = null
    fun recargarPosts() {
        if (imagenAbierta != null) return

        perfilActualId?.let { id ->
            viewModelScope.launch {
                cargarPosts(id)
            }
        }
    }

    fun eliminarPost(postId: String) {
        viewModelScope.launch {
            try {
                db.collection("posts").document(postId).delete().await()
                recargarPosts()

            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun subirFotoPerfil(context: Context, uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val url = subirACloudinary(context, uri)
                if (url != null) {
                    db.collection("users").document(userId)
                        .update("avatarUrl", url).await()
                    fotoPerfil = url
                    actualizarDatosEnPosts(
                        uid = userId,
                        foto = url
                    )
                } else {
                    errorMessage = "Error subiendo imagen"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
            isLoading = false
        }
    }
    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .get()
                    .await()

                usuariosFeed = snapshot.documents.map { doc ->
                    mapOf(
                        "uid" to doc.id,
                        "usuario" to (doc.getString("usuario") ?: ""),
                        "nombre" to (doc.getString("nombrepublico") ?: ""),
                        "foto" to (doc.getString("avatarUrl") ?: "")
                    )
                }

            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
    fun verificarSiSigue(otroUid: String) {
        if (siguiendoListCache.isNotEmpty()) {
            sigueAlUsuario = otroUid in siguiendoListCache
            return
        }
        val miUid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(miUid).get().await()
                siguiendoListCache = (doc.get("siguiendoList") as? List<*>)
                    ?.mapNotNull { it as? String }
                    ?: emptyList()
                sigueAlUsuario = otroUid in siguiendoListCache
            } catch (_: Exception) { }
        }
    }

    fun toggleSeguir(otroUid: String) {
        if (procesandoSeguir) return
        val miUid = auth.currentUser?.uid ?: return
        val notifId = "seguir_${miUid}_${otroUid}"  // 👈 ID predecible

        viewModelScope.launch {
            procesandoSeguir = true
            try {
                val miRef = db.collection("users").document(miUid)
                val otroRef = db.collection("users").document(otroUid)

                if (sigueAlUsuario) {
                    miRef.update(
                        mapOf(
                            "siguiendoList" to FieldValue.arrayRemove(otroUid),
                            "followingCount" to FieldValue.increment(-1)
                        )
                    ).await()
                    otroRef.update(
                        mapOf(
                            "seguidoresList" to FieldValue.arrayRemove(miUid),
                            "followersCount" to FieldValue.increment(-1)
                        )
                    ).await()
                    db.collection("notificaciones").document(notifId).delete().await()  // 👈 delete directo
                    siguiendoListCache = siguiendoListCache - otroUid
                    sigueAlUsuario = false
                    seguidores = maxOf(0, seguidores - 1)
                } else {
                    miRef.update(
                        mapOf(
                            "siguiendoList" to FieldValue.arrayUnion(otroUid),
                            "followingCount" to FieldValue.increment(1)
                        )
                    ).await()
                    otroRef.update(
                        mapOf(
                            "seguidoresList" to FieldValue.arrayUnion(miUid),
                            "followersCount" to FieldValue.increment(1)
                        )
                    ).await()
                    val miDoc = db.collection("users").document(miUid).get().await()
                    val notif = mapOf(
                        "tipo" to "seguir",
                        "remitenteId" to miUid,
                        "remitenteNombre" to (miDoc.getString("nombrepublico") ?: ""),
                        "remitenteFoto" to (miDoc.getString("avatarUrl") ?: ""),
                        "destinatarioId" to otroUid,
                        "visto" to false,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("notificaciones").document(notifId).set(notif).await()
                    siguiendoListCache = siguiendoListCache + otroUid
                    sigueAlUsuario = true
                    seguidores += 1
                }
            } catch (_: Exception) { }
            procesandoSeguir = false
        }
    }

    fun refrescarPostsLike(postId: String, userId: String, liked: Boolean) {
        postsNormales = postsNormales.map { post ->
            if (post.id == postId) {
                if (liked) {
                    post.copy(
                        likes = post.likes + 1,
                        likedBy = post.likedBy + userId
                    )
                } else {
                    post.copy(
                        likes = maxOf(0, post.likes - 1),
                        likedBy = post.likedBy - userId
                    )
                }
            } else post
        }
    }

    fun quitarFotoPerfil() {
        val uid = auth.currentUser?.uid ?: return

        fotoPerfil = null

        viewModelScope.launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update("avatarUrl", "")
                    .await()
                actualizarDatosEnPosts(
                    uid = uid,
                    foto = ""
                )
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun actualizarPerfil(
        nombrePublico: String,
        usuario: String
    ) {
        val uid = auth.currentUser?.uid ?: return

        if (nombrePublico.isBlank()) {
            errorMessage = "El nombre no puede estar vacío"
            return
        }

        if (usuario.isBlank()) {
            errorMessage = "El usuario no puede estar vacío"
            return
        }

        viewModelScope.launch {
            try {

                db.collection("users")
                    .document(uid)
                    .update(
                        mapOf(
                            "nombrepublico" to nombrePublico.trim(),
                            "usuario" to usuario.trim()
                        )
                    )
                    .await()
                actualizarDatosEnPosts(
                    uid = uid,
                    nombre = nombrePublico,
                    usuario = usuario
                )
                this@PerfilViewModel.nombrepublico = nombrePublico.trim()
                this@PerfilViewModel.usuario = usuario.trim()

            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
    private suspend fun actualizarDatosEnPosts(
        uid: String,
        nombre: String? = null,
        usuario: String? = null,
        foto: String? = null
    ) {
        val posts = db.collection("posts")
            .whereEqualTo("autorId", uid)
            .get()
            .await()

        posts.documents.forEach { post ->
            val updates = mutableMapOf<String, Any>()

            nombre?.let { updates["autorNombre"] = it }
            usuario?.let { updates["autorUsuario"] = it }
            foto?.let { updates["autorFoto"] = it }

            if (updates.isNotEmpty()) {
                post.reference.update(updates).await()
            }
        }
    }
}