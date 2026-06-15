package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_sketch.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun UsuariosListaSheet(
    tipo: String,
    usuarioId: String,
    onDismiss: () -> Unit,
    onPerfilClick: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var usuarios by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    LaunchedEffect(tipo, usuarioId) {
        try {
            val doc = db.collection("users").document(usuarioId).get().await()
            val campo = if (tipo == "seguidores") "seguidoresList" else "siguiendoList"
            val lista = (doc.get(campo) as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            usuarios = lista.mapNotNull { uid ->
                val u = db.collection("users").document(uid).get().await()
                mapOf(
                    "uid" to uid,
                    "nombre" to (u.getString("nombrepublico") ?: ""),
                    "usuario" to (u.getString("usuario") ?: ""),
                    "foto" to (u.getString("avatarUrl") ?: "")
                )
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(enabled = false) {}
        ) {
            Text(
                if (tipo == "seguidores") "Seguidores" else "Siguiendo",
                modifier = Modifier.padding(16.dp),
                style = TextStyle(fontWeight = FontWeight(700), fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(Font(R.font.inter_regular)))
            )
            HorizontalDivider()
            LazyColumn {
                items(usuarios) { u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPerfilClick(u["uid"] ?: "") }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = u["foto"],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            error = painterResource(R.drawable.poordrawnplaceholder),
                            placeholder = painterResource(R.drawable.poordrawnplaceholder)
                        )
                        Column {
                            Text(u["nombre"] ?: "", style = TextStyle(fontWeight = FontWeight(600),
                                fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))))
                            Text("@${u["usuario"]}", style = TextStyle(fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily(Font(R.font.inter_regular))))
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

