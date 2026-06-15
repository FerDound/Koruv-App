package com.example.project_sketch

import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class Mensaje(
    val id: String = "",
    val texto: String = "",
    val autorId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val leido: Boolean = false,
    val recibido: Boolean = false
)

data class Chat(
    val id: String = "",
    val participantes: List<String> = emptyList(),
    val ultimoMensaje: String = "",
    val ultimoTimestamp: Long = 0L,
    val noLeidos: Map<String, Int> = emptyMap(),
    val ultimoAutorId: String = ""
)

class ChatsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val miUid get() = auth.currentUser?.uid ?: ""

    var chats by mutableStateOf<List<Pair<Chat, Map<String, String>>>>(emptyList())
        private set
    var mensajes by mutableStateOf<List<Mensaje>>(emptyList())
        private set
    var cargandoMas by mutableStateOf(false)
        private set
    var hayMasMensajes by mutableStateOf(true)
        private set

    private var primerDocumento: DocumentSnapshot? = null
    private var listenerChats: ListenerRegistration? = null
    private var listenerMensajes: ListenerRegistration? = null

    fun escucharChats() {
        listenerChats?.remove()
        listenerChats = db.collection("chats")
            .whereArrayContains("participantes", miUid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                viewModelScope.launch {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        val chat = doc.toObject(Chat::class.java)?.copy(id = doc.id)
                            ?: return@mapNotNull null
                        val otroUid = chat.participantes.firstOrNull { it != miUid } ?: return@mapNotNull null
                        val userDoc = db.collection("users").document(otroUid).get().await()
                        val datosUsuario = mapOf(
                            "uid" to otroUid,
                            "nombre" to (userDoc.getString("nombrepublico") ?: ""),
                            "foto" to (userDoc.getString("avatarUrl") ?: "")
                        )
                        chat to datosUsuario
                    }.sortedByDescending { it.first.ultimoTimestamp }
                    chats = lista
                }
            }
    }

    fun escucharMensajes(chatId: String) {
        mensajes = emptyList()
        primerDocumento = null
        hayMasMensajes = true

        viewModelScope.launch {
            try {
                val snapshot = db.collection("chats").document(chatId)
                    .collection("mensajes")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(20)
                    .get(Source.SERVER).await()

                primerDocumento = snapshot.documents.lastOrNull()
                mensajes = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Mensaje::class.java)?.copy(id = doc.id)
                }.sortedBy { it.timestamp }

                if (snapshot.documents.size < 20) hayMasMensajes = false
            } catch (_: Exception) { }
        }

        listenerMensajes?.remove()
        listenerMensajes = db.collection("chats").document(chatId)
            .collection("mensajes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, _ ->
                val doc = snapshot?.documents?.firstOrNull() ?: return@addSnapshotListener
                val nuevo = doc.toObject(Mensaje::class.java)?.copy(id = doc.id) ?: return@addSnapshotListener
                if (mensajes.none { it.id == nuevo.id }) {
                    mensajes = mensajes + nuevo
                    if (nuevo.autorId != miUid) {
                        viewModelScope.launch {
                            doc.reference.update(mapOf("recibido" to true, "leido" to true)).await()
                        }
                    }
                }
            }
    }

    fun cargarMasMensajes(chatId: String) {
        if (!hayMasMensajes || cargandoMas) return
        viewModelScope.launch {
            cargandoMas = true
            try {
                val primero = primerDocumento ?: return@launch
                val snapshot = db.collection("chats").document(chatId)
                    .collection("mensajes")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(primero)
                    .limit(20)
                    .get(Source.SERVER).await()

                if (snapshot.isEmpty || snapshot.documents.size < 20) {
                    hayMasMensajes = false
                } else {
                    primerDocumento = snapshot.documents.lastOrNull()
                    val viejos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Mensaje::class.java)?.copy(id = doc.id)
                    }
                    mensajes = (viejos + mensajes).sortedBy { it.timestamp }
                    if (snapshot.size() < 20) hayMasMensajes = false
                }
            } catch (_: Exception) { }
            cargandoMas = false
        }
    }

    fun enviarMensaje(chatId: String, texto: String, destinatarioId: String) {
        if (texto.isBlank()) return
        viewModelScope.launch {
            try {
                val mensaje = Mensaje(
                    texto = texto.trim(),
                    autorId = miUid,
                    timestamp = System.currentTimeMillis()
                )
                db.collection("chats").document(chatId)
                    .collection("mensajes").add(mensaje).await()

                val chatRef = db.collection("chats").document(chatId)
                val chatDoc = chatRef.get().await()
                val noLeidos = (chatDoc.get("noLeidos") as? Map<*, *>)
                    ?.mapKeys { it.key.toString() }
                    ?.mapValues { (it.value as? Long)?.toInt() ?: 0 }
                    ?.toMutableMap() ?: mutableMapOf()
                noLeidos[destinatarioId] = (noLeidos[destinatarioId] ?: 0) + 1

                chatRef.update(
                    "ultimoMensaje", texto,
                    "ultimoTimestamp", mensaje.timestamp,
                    "noLeidos", noLeidos,
                    "ultimoAutorId", miUid
                ).await()
            } catch (_: Exception) { }
        }
    }

    fun abrirOCrearChat(otroUid: String, onChatListo: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val existente = db.collection("chats")
                    .whereArrayContains("participantes", miUid)
                    .get().await()
                    .documents.firstOrNull { doc ->
                        val participantes = doc.get("participantes")
                        if (participantes is List<*>) {
                            participantes.contains(otroUid)
                        } else false
                    }


                val chatId = if (existente != null) {
                    existente.id
                } else {
                    val nuevoChat = Chat(
                        participantes = listOf(miUid, otroUid),
                        noLeidos = mapOf(miUid to 0, otroUid to 0)
                    )

                    db.collection("chats").add(nuevoChat).await().id
                }
                withContext(Dispatchers.Main) {
                    onChatListo(chatId)
                }
            } catch (_: Exception) { }
        }
    }

    fun marcarLeido(chatId: String) {
        viewModelScope.launch {
            try {
                val chatRef = db.collection("chats").document(chatId)

                val chatDoc = chatRef.get().await()
                val noLeidos = (chatDoc.get("noLeidos") as? Map<*, *>)
                    ?.mapKeys { it.key.toString() }
                    ?.mapValues { (it.value as? Long)?.toInt() ?: 0 }
                    ?.toMutableMap() ?: mutableMapOf()
                noLeidos[miUid] = 0
                chatRef.update("noLeidos", noLeidos).await()
                val mensajesNoLeidos = chatRef.collection("mensajes")
                    .whereEqualTo("autorId", miUid.let {
                        chatDoc.get("participantes").let { p ->
                            (p as? List<*>)?.firstOrNull { it != miUid }?.toString() ?: ""
                        }
                    })
                    .whereEqualTo("leido", false)
                    .get().await()
                mensajesNoLeidos.documents.forEach { doc ->
                    doc.reference.update(mapOf("recibido" to true, "leido" to true)).await()
                }
            } catch (_: Exception) { }
        }
    }

    override fun onCleared() {
        listenerChats?.remove()
        listenerMensajes?.remove()
    }
}