package com.example.project_sketch.ui.pantallas

import android.net.Uri
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_sketch.ComentariosViewModel
import com.example.project_sketch.NotisViewModel
import com.example.project_sketch.Post
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.CampoMensaje
import com.example.project_sketch.ui.login.ComentariosFormato
import com.example.project_sketch.ui.login.Dimensiones
import com.example.project_sketch.ui.login.fechaFormateada
import com.example.project_sketch.ui.theme.BlancoPuro
import com.google.firebase.auth.FirebaseAuth
import kotlin.collections.contains

@Composable
fun PostAbiertoPantalla(
    post: Post,
    posts: List<Post> = emptyList(),
    viewModel: NotisViewModel,
    onItemClick: (String) -> Unit = {},
    onEliminar: () -> Unit = {},
    abrirTeclado: Boolean = false,
    siguiendoMap: Map<String, Boolean> = emptyMap(),
    onSeguirClick: (String) -> Unit = {},
    onPerfilClick : (String) -> Unit = {}
) {
    var indiceActual by remember(post.id) {
        mutableIntStateOf(posts.indexOfFirst { it.id == post.id })
    }
    var texto by remember { mutableStateOf("") }
    val postActual = posts.find { it.id == (posts.getOrNull(indiceActual)?.id ?: post.id) } ?: post
    val comentariosViewModel: ComentariosViewModel = viewModel()
    var imagenAmpliada by remember { mutableStateOf(false) }
    val altopantalla = Dimensiones.altoPantalla()
    var escala by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val yaLikeo = userId in postActual.likedBy
    var menuExpandido by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val esMiPost = postActual.autorId == userId

    val swipeModifier = Modifier.pointerInput(indiceActual) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount < -50 && indiceActual < posts.size - 1) indiceActual++
            else if (dragAmount > 50 && indiceActual > 0) indiceActual--
        }
    }
    LaunchedEffect(postActual.id) {
        comentariosViewModel.escucharComentarios(postActual.id)
    }
    LaunchedEffect(comentariosViewModel.comentarios.size) {
        val total = comentariosViewModel.comentarios.size
        if (total > 0) {
            val itemIndex = listState.layoutInfo.totalItemsCount - 1
            if (itemIndex >= 0) listState.animateScrollToItem(itemIndex)
        }
    }
    LaunchedEffect(abrirTeclado) {
        if (abrirTeclado) {
            kotlinx.coroutines.delay(300)
            focusRequester.requestFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .then(swipeModifier),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    HorizontalDivider(color = colorScheme.outline)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onPerfilClick(postActual.autorId) }
                        ) {
                            AsyncImage(
                                model = postActual.autorFoto,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.poordrawnplaceholder),
                                placeholder = painterResource(R.drawable.poordrawnplaceholder),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Column {
                                Text(
                                    text = postActual.autorNombre,
                                    modifier = Modifier
                                        .widthIn(max = 180.dp)
                                        .basicMarquee(
                                            iterations = Int.MAX_VALUE,
                                            initialDelayMillis = 2000,
                                            repeatDelayMillis = 2000,
                                            spacing = MarqueeSpacing(20.dp)
                                        ),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onBackground,
                                        fontFamily = FontFamily(Font(R.font.inter_regular))
                                    ),
                                )

                            }
                        }
                        Box {
                            if (esMiPost) {
                                IconButton(onClick = { menuExpandido = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.opciones),
                                        contentDescription = "Opciones",
                                        tint = colorScheme.onSurfaceVariant
                                    )
                                }
                                DropdownMenu(
                                    expanded = menuExpandido,
                                    onDismissRequest = { menuExpandido = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Editar") },
                                        onClick = {
                                            menuExpandido = false
                                            onItemClick("editar/${post.id}?texto=${Uri.encode(post.texto)}")
                                        },
                                        modifier = Modifier.background(colorScheme.background)
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Eliminar") },
                                        onClick = {
                                            menuExpandido = false
                                            viewModel.eliminarPost(post.id)
                                            onEliminar()
                                        }
                                    )
                                }
                            } else {
                                val yaSigue = siguiendoMap[postActual.autorId] ?: false
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (yaSigue) Color.LightGray else colorScheme.primary)
                                        .clickable { onSeguirClick(postActual.autorId) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (yaSigue) "Siguiendo" else "Seguir",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                                            fontWeight = FontWeight(600),
                                            color = colorScheme.background
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    HorizontalDivider(color = colorScheme.outline)
                    if (!postActual.imagenUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = postActual.imagenUrl,
                            contentDescription = postActual.texto,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(altopantalla * 0.27262F)
                                .clickable { imagenAmpliada = true }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                item {
                    Column(
                        modifier = if (!postActual.imagenUrl.isNullOrEmpty()) {
                            Modifier.padding(horizontal = 16.dp)
                        } else {
                            Modifier.padding(16.dp)
                        }
                    ) {
                        Text(
                            text = postActual.texto,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 26.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = colorScheme.onBackground,
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fechaFormateada(postActual.timestamp),
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = colorScheme.tertiary,
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                modifier = Modifier.size(20.dp),
                                onClick = {
                                    viewModel.darLike(postActual.id)
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    painter = if (yaLikeo) painterResource(R.drawable.corazonlike) else painterResource(R.drawable.corazon),
                                    contentDescription = "Me gusta",
                                    tint = if (yaLikeo) colorScheme.secondary else colorScheme.tertiary
                                )
                            }
                            Text(
                                text = "${postActual.likes}",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = colorScheme.tertiary,
                                    fontFamily = FontFamily(Font(R.font.inter_regular))
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    HorizontalDivider(color = colorScheme.outline)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Respuestas",
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                        Text(
                            text = "${postActual.comentarios} comentarios",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.tertiary,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                    }
                    HorizontalDivider(color = colorScheme.outline)
                }
                item {
                    ComentariosFormato(
                        comentarios = comentariosViewModel.comentarios,
                        onLike = { comentariosViewModel.darLikeComentario(postActual.id, it) },
                        onEliminarComentario = { comentariosViewModel.eliminarComentario(postActual.id, it) },
                        onPerfilClick = onPerfilClick
                    )
                    HorizontalDivider(color = colorScheme.outline)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(12.dp)
            ) {
                HorizontalDivider(color = colorScheme.outline)
                CampoMensaje(
                    valor = texto,
                    onValorChange = { texto = it },
                    onEnviar = {
                        comentariosViewModel.hacerComentario(postActual.id, texto)
                        viewModel.actualizarComentariosPost(postActual.id)
                        texto = ""
                    },
                    textFieldModifier = Modifier.focusRequester(focusRequester)
                )
            }
        }
    }
    if (imagenAmpliada && !postActual.imagenUrl.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background.copy(alpha = 0.8f))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        escala = (escala * zoom).coerceIn(1f, 5f)
                        offset = if (escala == 1f) Offset.Zero else offset + pan
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = postActual.imagenUrl,
                contentDescription = postActual.texto,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = escala,
                        scaleY = escala,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
            IconButton(
                onClick = { imagenAmpliada = false; escala = 1f; offset = Offset.Zero },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = BlancoPuro)
            }
        }
    }
}
