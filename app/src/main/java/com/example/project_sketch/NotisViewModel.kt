package com.example.project_sketch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.pow

data class Notificacion(
    val id: String = "",
    val tipo: String = "",
    val remitenteId: String = "",
    val remitenteNombre: String = "",
    val remitenteFoto: String = "",
    val destinatarioId: String = "",
    val postId: String? = null,
    val visto: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class NotisViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var ultimoDocumento: DocumentSnapshot? = null
    var hayMasPosts by mutableStateOf(true)
        private set
    var cargandoMas by mutableStateOf(false)
        private set
    var cargandoFeed by mutableStateOf(true)
        private set
    var notificaciones by mutableStateOf<List<Notificacion>>(emptyList())
        private set
    var postsFeed by mutableStateOf<List<Post>>(emptyList())
        private set
    var postsLikeados by mutableStateOf<List<Post>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var errorCargandoMas by mutableStateOf(false)
        private set
    var siguiendoMap by mutableStateOf<Map<String, Boolean>>(emptyMap())
        private set
    var seguidoresMap by mutableStateOf<Map<String, Boolean>>(emptyMap())
        private set
    var siguiendoListCache = emptyList<String>()
    var seguidoresListCache = emptyList<String>()

    var recargas by mutableIntStateOf(0)
        private set



    init {
        cargarFeed()
        cargarNotificaciones()
    }

    private fun rankearPosts() {
        val ahora = System.currentTimeMillis()
        postsFeed = postsFeed.sortedByDescending { post ->
            val horasDesdePublicacion = (ahora - post.timestamp) / (1000.0 * 60 * 60)
            val pesoLikes = post.likes * 2.0
            val pesoComentarios = post.comentarios * 1.5
            val decaimiento = (horasDesdePublicacion + 2).pow(1.8)
            (pesoLikes + pesoComentarios) / decaimiento
        }
    }
    private var ultimaRecarga = 0L
    fun cargarFeed() {
        val ahora = System.currentTimeMillis()
        if (ahora - ultimaRecarga < 60_000 && postsFeed.isNotEmpty()) return
        ultimaRecarga = ahora
        viewModelScope.launch {
            cargandoFeed = true
            recargas++
            ultimoDocumento = null
            hayMasPosts = true
            errorCargandoMas = false
            val cacheSnapshot = try {
                db.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(7)
                    .get(Source.CACHE)
                    .await()
            } catch (_: Exception) { null }

            if (cacheSnapshot != null && !cacheSnapshot.isEmpty) {
                postsFeed = cacheSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }
                rankearPosts()
            }

            try {
                val snapshot = db.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(7)
                    .get(Source.SERVER)
                    .await()
                ultimoDocumento = snapshot.documents.lastOrNull()
                postsFeed = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }
                if (snapshot.documents.size < 7) hayMasPosts = false
                rankearPosts()
                cargarSiguiendoMap(postsFeed.map { it.autorId }.distinct())
            } catch (_: Exception) {
                errorMessage = "Sin conexión"
            } finally {
                cargandoFeed = false
            }
        }
    }

    fun cargarMas() {
        if (!hayMasPosts || cargandoMas || postsFeed.isEmpty()) return
        viewModelScope.launch {
            errorCargandoMas = false
            cargandoMas = true

            val ultimo = ultimoDocumento
            if (ultimo == null) {
                errorCargandoMas = true
                cargandoMas = false
                return@launch
            }

            try {
                val snapshot = db.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(ultimo)
                    .limit(5)
                    .get(Source.SERVER).await()

                if (snapshot.isEmpty) {
                    hayMasPosts = false
                } else {
                    ultimoDocumento = snapshot.documents.lastOrNull()
                    val nuevos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)?.copy(id = doc.id)
                    }
                    postsFeed = postsFeed + nuevos
                    if (nuevos.size < 5) hayMasPosts = false
                    rankearPosts()
                    cargarSiguiendoMap(postsFeed.map { it.autorId }.distinct())
                }
            } catch (_: Exception) {
                errorCargandoMas = true
            }

            cargandoMas = false
        }
    }
    private var listenerNotis: ListenerRegistration? = null
    fun cargarNotificaciones() {
        val userId = auth.currentUser?.uid ?: return
        listenerNotis?.remove()
        listenerNotis = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20) // ← limita a 20 notifs recientes
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val limite = System.currentTimeMillis() - (48 * 60 * 60 * 1000L)
                notificaciones = snapshot.documents.mapNotNull { doc ->
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    if (timestamp < limite) return@mapNotNull null
                    doc.toObject(Notificacion::class.java)?.copy(id = doc.id)
                }
            }
    }
    override fun onCleared() {
        listenerNotis?.remove()
    }

    fun cargarLikeados(userId: String? = null) {
        val uid = userId ?: FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("posts")
                    .whereArrayContains("likedBy", uid)
                    .get().await()
                postsLikeados = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.timestamp }
            } catch (e: Exception) { errorMessage = e.message }
        }
    }

    fun marcarVisto(notifId: String) {
        viewModelScope.launch {
            try {
                db.collection("notificaciones").document(notifId)
                    .update("visto", true).await()
                notificaciones = notificaciones.map {
                    if (it.id == notifId) it.copy(visto = true) else it
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
    fun darLike(postId: String) {
        val userId = auth.currentUser?.uid ?: return
        val postLocal = postsFeed.find { it.id == postId } ?: return
        val likedBy = postLocal.likedBy
        val autorId = postLocal.autorId
        viewModelScope.launch {
            try {
                val remitenteDoc = db.collection("users").document(userId).get().await()
                val remitenteNombre = remitenteDoc.getString("nombrepublico") ?: ""
                val remitenteFoto = remitenteDoc.getString("avatarUrl") ?: ""
                val ref = db.collection("posts").document(postId)
                if (userId in likedBy) {
                    ref.update(
                        "likedBy", likedBy - userId,
                        "likes", (postLocal.likes - 1).coerceAtLeast(0))
                        .await()
                    postsFeed = postsFeed.map { post ->
                        if (post.id == postId) post.copy(likes = maxOf(0, post.likes - 1),
                            likedBy = post.likedBy - userId
                        ) else post }
                    db.collection("notificaciones")
                        .document("like_${userId}_${postId}").delete().await()
                } else {
                    ref.update(
                        "likedBy", likedBy + userId,
                        "likes", postLocal.likes + 1
                    ).await()
                    postsFeed = postsFeed.map { post ->
                        if (post.id == postId) post.copy(
                            likes = post.likes + 1,
                            likedBy = post.likedBy + userId
                        ) else post }
                    if (autorId != userId) {
                        val notif = mapOf(
                            "tipo" to "like",
                            "remitenteId" to userId,
                            "remitenteNombre" to remitenteNombre,
                            "remitenteFoto" to remitenteFoto,
                            "destinatarioId" to autorId,
                            "postId" to postId,
                            "visto" to false,
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("notificaciones")
                            .document("like_${userId}_${postId}").set(notif).await()
                    }
                }
            } catch (e: Exception) { errorMessage = e.message }
        }
    }

    fun eliminarPost(postId: String) {
        viewModelScope.launch {
            try {
                db.collection("posts").document(postId).delete().await()
                postsFeed = postsFeed.filter { it.id != postId }
                rankearPosts()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun toggleSeguirPost(otroUid: String) {
        val miUid = auth.currentUser?.uid ?: return
        val sigueActual = siguiendoMap[otroUid] ?: false
        siguiendoMap = (siguiendoMap + (otroUid to !sigueActual)).toMap()
        siguiendoListCache = if (sigueActual) {
            siguiendoListCache - otroUid
        } else {
            siguiendoListCache + otroUid
        }
        viewModelScope.launch {
            try {
                val miRef = db.collection("users").document(miUid)
                if (sigueActual) {
                    miRef.update(
                        "siguiendoList", com.google.firebase.firestore.FieldValue.arrayRemove(otroUid),
                        "followingCount", com.google.firebase.firestore.FieldValue.increment(-1)
                    ).await()
                    db.collection("users").document(otroUid).update(
                        "seguidoresList", com.google.firebase.firestore.FieldValue.arrayRemove(miUid),
                        "followersCount", com.google.firebase.firestore.FieldValue.increment(-1)
                    ).await()
                    db.collection("notificaciones")
                        .document("seguir_${miUid}_${otroUid}").delete().await()
                } else {
                    miRef.update(
                        "siguiendoList", com.google.firebase.firestore.FieldValue.arrayUnion(otroUid),
                        "followingCount", com.google.firebase.firestore.FieldValue.increment(1)
                    ).await()
                    db.collection("users").document(otroUid).update(
                        "seguidoresList", com.google.firebase.firestore.FieldValue.arrayUnion(miUid),
                        "followersCount", com.google.firebase.firestore.FieldValue.increment(1)
                    ).await()
                    val miPerfilDoc = db.collection("users").document(miUid).get().await()
                    val notif = mapOf(
                        "tipo" to "seguir",
                        "remitenteId" to miUid,
                        "remitenteNombre" to (miPerfilDoc.getString("nombrepublico") ?: ""),
                        "remitenteFoto" to (miPerfilDoc.getString("avatarUrl") ?: ""),
                        "destinatarioId" to otroUid,
                        "visto" to false,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("notificaciones")
                        .document("seguir_${miUid}_${otroUid}").set(notif).await()
                }
            } catch (_: Exception) { }
        }
    }
    private var siguiendoCargado = false

    private suspend fun cargarSiguiendoMap(uids: List<String>) {
        val miUid = auth.currentUser?.uid ?: return
        try {
            if (!siguiendoCargado) { // ← solo carga una vez por sesión
                val doc = db.collection("users").document(miUid).get().await()
                siguiendoListCache = (doc.get("siguiendoList") as? List<*>)
                    ?.mapNotNull { it as? String } ?: emptyList()
                seguidoresListCache = (doc.get("seguidoresList") as? List<*>)
                    ?.mapNotNull { it as? String } ?: emptyList()
                siguiendoCargado = true
            }
            siguiendoMap = uids.associateWith { it in siguiendoListCache }
            seguidoresMap = uids.associateWith { it in seguidoresListCache }
        } catch (_: Exception) {}
    }

    fun actualizarComentariosPost(postId: String) {
        postsFeed = postsFeed.map { post ->
            if (post.id == postId) {
                post.copy(comentarios = post.comentarios + 1)
            } else post
        }
    }

    fun resetearEstadoPaginacion() {
        hayMasPosts = false
    }
}