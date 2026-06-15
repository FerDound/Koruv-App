package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.project_sketch.NotisViewModel
import com.example.project_sketch.PerfilViewModel
import com.example.project_sketch.Post
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.CampoDeDatos
import com.example.project_sketch.ui.login.perfilesFormato
import com.example.project_sketch.ui.login.postFormato


@Composable
fun BuscarPantalla(
    viewModel: PerfilViewModel = viewModel(),
    notisViewModel: NotisViewModel,
    onItemClick: (String) -> Unit,
    onPerfilClick: (String) -> Unit,
    onPostClick: (Post) -> Unit,
    onComentarClick: (Post) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var ordenActual by remember { mutableStateOf("nuevos") }
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    val postsFiltrados = remember(busqueda, notisViewModel.postsFeed, notisViewModel.siguiendoMap) {
        val filtrados = if (busqueda.isBlank()) {
            notisViewModel.postsFeed
        } else {
            notisViewModel.postsFeed.filter {
                it.texto.contains(busqueda, ignoreCase = true) ||
                        it.autorNombre.contains(busqueda, ignoreCase = true)
            }.take(3)
        }
        filtrados.sortedByDescending { it.timestamp }
    }

    val usuariosFiltrados = remember(busqueda, viewModel.usuariosFeed) {
        if (busqueda.isBlank()) emptyList()
        else viewModel.usuariosFeed.filter { usuario ->
            (usuario["usuario"] ?: "")
                .contains(busqueda, ignoreCase = true) ||
                    (usuario["nombre"] ?: "")
                        .contains(busqueda, ignoreCase = true)
        }.take(5)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CampoDeDatos(
                        "Buscar en Koruv...",
                        busqueda,
                        { busqueda = it },
                        iconPainter = painterResource(R.drawable.buscar)
                    )
                }
            }
        }

        if (busqueda.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background)
                        .padding(start = 16.dp, end = 22.dp, top = 22.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PERSONAS",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = colorScheme.onBackground,
                            letterSpacing = 0.7.sp,
                        )
                    )
                }
            }
            perfilesFormato(
                usuarios = usuariosFiltrados,
                onPerfilClick = { usuario ->
                    usuario["uid"]?.let { uid ->
                        onPerfilClick(uid)
                    }
                }
            )
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background)
                        .padding(start = 16.dp, end = 22.dp, top = 22.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PUBLICACIONES",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = colorScheme.onBackground,
                            letterSpacing = 0.7.sp,
                        )
                    )
                }
            }
            postFormato(
                posts = postsFiltrados,
                ordenActual = ordenActual,
                onOrdenChange = { ordenActual = it },
                onLike = { notisViewModel.darLike(it) },
                onEliminar = { notisViewModel.eliminarPost(it) },
                onItemClick = onItemClick,
                onPostClick = onPostClick,
                onPerfilClick = onPerfilClick,
                onSeguirClick = { notisViewModel.toggleSeguirPost(it) },
                siguiendoMap = notisViewModel.siguiendoMap,
                botonesfiltrado = false,
                onComentarClick = onComentarClick
            )
        }
    }
}