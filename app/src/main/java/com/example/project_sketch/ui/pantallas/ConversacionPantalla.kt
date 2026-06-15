package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sketch.ChatsViewModel
import com.example.project_sketch.ui.login.CampoMensaje
import com.example.project_sketch.ui.login.MensajesBurbuja

@Composable
fun ConversacionPantalla(
    chatId: String,
    otroNombre: String,
    otroUid: String,
    otroFoto: String = "",
    onPerfilClick: (String) -> Unit = {},
    viewModel: ChatsViewModel = viewModel()
) {
    var texto by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.escucharMensajes(chatId)
        viewModel.marcarLeido(chatId)
    }

    LaunchedEffect(viewModel.mensajes.size) {
        if (viewModel.mensajes.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.mensajes.size - 1)
        }
    }

    val cargarMas by remember {
        derivedStateOf {
            val primerVisible = listState.firstVisibleItemIndex
            primerVisible <= 2 && viewModel.mensajes.isNotEmpty()
        }
    }
    LaunchedEffect(cargarMas) {
        if (cargarMas && !viewModel.cargandoMas && viewModel.hayMasMensajes) {
            viewModel.cargarMasMensajes(chatId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surface)
    ) {
        if (viewModel.cargandoMas && viewModel.hayMasMensajes) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
        ) {
            items(viewModel.mensajes.size) { index ->
                val mensaje = viewModel.mensajes[index]
                val siguiente = viewModel.mensajes.getOrNull(index + 1)
                val anterior = viewModel.mensajes.getOrNull(index - 1)
                val mismoAutorSiguiente = siguiente?.autorId == mensaje.autorId
                val mismoAutorAnterior = anterior?.autorId == mensaje.autorId
                val mostrarFoto = !mismoAutorAnterior
                MensajesBurbuja(
                    propio = mensaje.autorId == viewModel.miUid,
                    texto = mensaje.texto,
                    hora = formatearHora(mensaje.timestamp),
                    leido = mensaje.leido,
                    recibido = mensaje.recibido,
                    fotoAutor = if (mensaje.autorId != viewModel.miUid) otroFoto else "",
                    mostrarFoto = mostrarFoto
                )
                Spacer(modifier = Modifier.height(
                    if (mismoAutorSiguiente) 1.dp else 6.dp
                ))
            }
        }
        var texto by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoMensaje(
                valor = texto,
                onValorChange = { texto = it },
                onEnviar = {
                    if (texto.isNotBlank()) {
                        viewModel.enviarMensaje(chatId, texto, otroUid)
                        texto = ""
                    }
                }
            )
        }
    }
}