package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sketch.ChatsViewModel
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.CampoDeDatos
import com.example.project_sketch.ui.login.ChatObjeto


@Composable
fun ChatsPantalla(
    viewModel: ChatsViewModel = viewModel(),
    onChatClick: (chatId: String, otroNombre: String, otroFoto: String, otroUid: String) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        viewModel.escucharChats()
    }

    val chatsFiltrados = remember(busqueda, viewModel.chats) {
        if (busqueda.isBlank()) viewModel.chats
        else viewModel.chats.filter { (_, usuario) ->
            usuario["nombre"]?.contains(busqueda, ignoreCase = true) == true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                CampoDeDatos(
                    "Buscar en chats...",
                    busqueda,
                    { busqueda = it },
                    iconPainter = painterResource(R.drawable.buscar)
                )
            }
        }

        if (chatsFiltrados.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¡Mensajea con un amigo! 💬",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(500),
                            color = colorScheme.tertiary
                        )
                    )
                }
            }
        } else {
            items(chatsFiltrados, key = { it.first.id }) { (chat, usuario) ->
                val noLeidos = chat.noLeidos[viewModel.miUid] ?: 0
                val otroUid = usuario["uid"] ?: ""
                val ultimoMensajeMio = chat.ultimoAutorId == viewModel.miUid
                val visto = (chat.noLeidos[otroUid] ?: 0) == 0

                ChatObjeto(
                    nombre = usuario["nombre"] ?: "",
                    texto = chat.ultimoMensaje,
                    sinLeer = noLeidos,
                    hora = formatearHora(chat.ultimoTimestamp),
                    foto = usuario["foto"] ?: "",
                    ultimoMensajeMio = ultimoMensajeMio,
                    visto = visto,
                    onChatButton = {
                        onChatClick(
                            chat.id,
                            usuario["nombre"] ?: "",
                            usuario["foto"] ?: "",
                            usuario["uid"] ?: ""
                        )
                    }
                )
            }
            item { HorizontalDivider(color = colorScheme.outline) }
        }
    }
}

fun formatearHora(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val ahora = System.currentTimeMillis()
    val diff = ahora - timestamp
    val dias = diff / (1000 * 60 * 60 * 24)
    return if (dias < 1) {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        sdf.format(java.util.Date(timestamp))
    } else {
        val sdf = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
        sdf.format(java.util.Date(timestamp))
    }
}

