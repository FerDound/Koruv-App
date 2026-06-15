package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sketch.NotisViewModel
import com.example.project_sketch.Post
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.postFormato

@Composable
fun MainFeedPantalla(
    viewModel: NotisViewModel = viewModel(),
    onPostClick: (Post) -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onPerfilClick: (String) -> Unit = {},
    pagina: Boolean = true,
    ordenInicial: String = "nuevos",
    onComentarClick: (Post) -> Unit = {}
) {
    var ordenActual by remember { mutableStateOf(ordenInicial) }
    val listState = rememberLazyListState()
    val recargasAlMontar = remember { viewModel.recargas }

    val postsFiltrados = remember(
        ordenActual,
        viewModel.postsFeed,
        viewModel.siguiendoMap,
        viewModel.seguidoresMap
    ) {
        when (ordenActual) {
            "viejos" -> viewModel.postsFeed.sortedBy { it.timestamp }

            "likes" -> viewModel.postsFeed.sortedByDescending { it.likes }

            "seguidos" -> viewModel.postsFeed
                .filter { post ->
                    viewModel.siguiendoMap[post.autorId] == true
                }
                .sortedByDescending { it.timestamp }

            "amigos" -> viewModel.postsFeed
                .filter { post ->
                    (viewModel.siguiendoMap[post.autorId] == true) &&
                            (viewModel.seguidoresMap[post.autorId] == true)
                }
                .sortedByDescending { it.timestamp }

            else -> viewModel.postsFeed.sortedByDescending { it.timestamp }
        }
    }

    val cargarMas by remember {
        derivedStateOf {
            val ultimoVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && viewModel.postsFeed.isNotEmpty() && !viewModel.cargandoFeed && ultimoVisible >= total - 4
        }
    }
    LaunchedEffect(Unit) {
        if (!pagina) viewModel.resetearEstadoPaginacion()
    }
    LaunchedEffect(cargarMas) {
        if (cargarMas && !viewModel.cargandoMas && !viewModel.errorCargandoMas) {
            viewModel.cargarMas()
        }
    }

    LaunchedEffect(cargarMas, viewModel.errorCargandoMas) {
        if (cargarMas && viewModel.errorCargandoMas && !viewModel.cargandoMas) {
            kotlinx.coroutines.delay(3000)
            viewModel.cargarMas()
        }
    }

    LaunchedEffect(viewModel.recargas) {
        if (viewModel.recargas > recargasAlMontar) {
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (viewModel.cargandoFeed) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            }
        } else {
            postFormato(
                posts = postsFiltrados,
                ordenActual = ordenActual,
                onOrdenChange = { ordenActual = it },
                onLike = { viewModel.darLike(it) },
                onEliminar = { viewModel.eliminarPost(it) },
                onItemClick = onItemClick,
                onPostClick = onPostClick,
                onPerfilClick = onPerfilClick,
                onSeguirClick = { viewModel.toggleSeguirPost(it) },
                siguiendoMap = viewModel.siguiendoMap,
                onComentarClick = onComentarClick,
                botonesfiltrado = false
            )
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        viewModel.cargandoMas || viewModel.errorCargandoMas ->
                            CircularProgressIndicator(color = colorScheme.primary)
                        !viewModel.hayMasPosts || viewModel.postsFeed.isEmpty() ->
                            Text(
                                text = if (viewModel.postsFeed.isEmpty()) "No hay publicaciones aún 👀" else "Ya viste todo 👀",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    color = colorScheme.tertiary
                                )
                            )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
fun MainFeedPantallaPreview() {
    MainFeedPantalla()
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun MainFeedPantallaPreviewChica() {
    MainFeedPantalla()
}