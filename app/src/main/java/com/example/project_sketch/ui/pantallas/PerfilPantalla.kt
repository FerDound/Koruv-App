package com.example.project_sketch.ui.pantallas

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sketch.PerfilViewModel
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.Etiquetas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.example.project_sketch.ChatsViewModel
import com.example.project_sketch.NotisViewModel
import com.example.project_sketch.Post
import com.example.project_sketch.ui.login.BotonAnimado
import com.example.project_sketch.ui.login.Dimensiones
import com.example.project_sketch.ui.login.guardarPostComoImagen
import com.example.project_sketch.ui.login.postFormato
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun PerfilPantalla(
    usuarioId: String? = null,
    viewModel: PerfilViewModel = viewModel(),
    notisViewModel: NotisViewModel = viewModel(),
    chatsViewModel: ChatsViewModel = viewModel(),
    onPostClick: (Post) -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onPerfilClick: (String) -> Unit = {},
    onComentarClick: (Post) -> Unit = {},
    onMensajearClick: (chatId: String, nombre: String, foto: String, otroUid: String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    val scope = rememberCoroutineScope()
    val headerLayer = rememberGraphicsLayer()
    var mostrarEditor by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.subirFotoPerfil(context, it) }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.recargarPosts()
        }
    }
    var mostrarLista by remember { mutableStateOf<String?>(null) }
    var mostrando by remember { mutableStateOf("Ideas") }
    var ordenActual by remember { mutableStateOf("nuevos") }
    val postsFiltrados = remember(ordenActual, viewModel.postsNormales) {
        when (ordenActual) {
            "viejos" -> viewModel.postsNormales.sortedBy { it.timestamp }
            "likes" -> viewModel.postsNormales.sortedByDescending { it.likes }
            else -> viewModel.postsNormales.sortedByDescending { it.timestamp }
        }
    }
    val likesFiltrados = remember(
        ordenActual,
        notisViewModel.postsLikeados
    ) {
        when (ordenActual) {
            "viejos" -> notisViewModel.postsLikeados.sortedBy { it.timestamp }
            "likes" -> notisViewModel.postsLikeados.sortedByDescending { it.likes }
            else -> notisViewModel.postsLikeados.sortedByDescending { it.timestamp }
        }
    }

    val esMiPerfil = usuarioId == null ||
            usuarioId == FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(usuarioId) {
        viewModel.cargarPerfil(usuarioId)
        if (!esMiPerfil) {
            viewModel.verificarSiSigue(usuarioId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .drawWithContent {
                        headerLayer.record { this@drawWithContent.drawContent() }
                        drawContent()
                    }
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!viewModel.fotoPerfil.isNullOrEmpty()) {
                        AsyncImage(
                            modifier = Modifier
                                .size(altoPantalla * 0.098F)
                                .clip(CircleShape),
                            model = viewModel.fotoPerfil,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.poordrawnplaceholder),
                            placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.poordrawnplaceholder),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(altoPantalla * 0.098F)
                                .clip(CircleShape)
                                .clickable(enabled = esMiPerfil) {
                                    launcher.launch("image/*")
                                }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            modifier = Modifier.padding(start = anchoPantalla * 0.048F),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = viewModel.nombrepublico,
                                style = TextStyle(
                                    fontSize = (anchoPantalla * 0.048F).value.sp,
                                    fontWeight = FontWeight(900),
                                    color = colorScheme.onBackground,
                                    fontFamily = FontFamily(Font(R.font.inter_regular))
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .width( anchoPantalla * 0.291F)
                                    .basicMarquee(
                                        iterations = Int.MAX_VALUE,
                                        initialDelayMillis = 2000,
                                        repeatDelayMillis = 2000,
                                        spacing = MarqueeSpacing(30.dp)
                                    )
                            )
                            Text(
                                text = "@${viewModel.usuario}",
                                style = TextStyle(
                                    fontSize = (anchoPantalla * 0.034F).value.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily(Font(R.font.inter_regular))
                                )
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (viewModel.semestre.isNotEmpty()) Etiquetas(viewModel.semestre, colorScheme.primary)
                            }
                        }
                        Column(
                            modifier = Modifier.padding(start = anchoPantalla * 0.048F).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {

                            if (!esMiPerfil) {
                                Text(
                                    if (viewModel.sigueAlUsuario) "Siguiendo" else "Seguir",
                                    color = if (viewModel.sigueAlUsuario) colorScheme.onBackground else colorScheme.background,
                                    style = TextStyle(
                                        fontSize = (anchoPantalla * 0.034F).value.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(600),
                                        color = colorScheme.background
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(start = anchoPantalla * 0.048F)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (viewModel.sigueAlUsuario) colorScheme.background else colorScheme.primary
                                        )
                                        .border(1.dp, if (viewModel.sigueAlUsuario) colorScheme.primary else colorScheme.background , RoundedCornerShape(20.dp))
                                        .clickable(enabled = !viewModel.procesandoSeguir) {
                                            viewModel.toggleSeguir(usuarioId)
                                        }
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .fillMaxWidth(),
                                )
                                Text(
                                    "Mensajear",
                                    color = colorScheme.onBackground,
                                    style = TextStyle(
                                        fontSize = (anchoPantalla * 0.034F).value.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(600),
                                        color = colorScheme.background
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(start = anchoPantalla * 0.048F)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(colorScheme.background)
                                        .border(1.dp, colorScheme.primary, RoundedCornerShape(20.dp))
                                        .clickable {
                                            chatsViewModel.abrirOCrearChat(usuarioId) { chatId ->
                                                onMensajearClick(chatId, viewModel.nombrepublico, viewModel.fotoPerfil ?: "", usuarioId)
                                            }
                                        }
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .fillMaxWidth(),
                                )
                            } else {
                                Text(
                                    "Editar perfil",
                                    color = colorScheme.onBackground,
                                    style = TextStyle(
                                        fontSize = (anchoPantalla * 0.034F).value.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(600),
                                        color = colorScheme.background
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(start = anchoPantalla * 0.048F)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(colorScheme.background)
                                        .border(1.dp, colorScheme.primary, RoundedCornerShape(20.dp))
                                        .clickable {
                                            mostrarEditor = true
                                        }
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .fillMaxWidth(),
                                )
                                Text(
                                    "Compartir",
                                    color = colorScheme.onBackground,
                                    style = TextStyle(
                                        fontSize = (anchoPantalla * 0.034F).value.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(600),
                                        color = colorScheme.background
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(start = anchoPantalla * 0.048F)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(colorScheme.background)
                                        .border(1.dp, colorScheme.primary, RoundedCornerShape(20.dp))
                                        .clickable {
                                            scope.launch {
                                                val bitmap = headerLayer.toImageBitmap().asAndroidBitmap()
                                                val exito = guardarPostComoImagen(context, bitmap)
                                                Toast.makeText(
                                                    context,
                                                    if (exito) "✓ Guardado en galería" else "Error al guardar",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .padding(top = 8.dp, bottom = 8.dp)
                                        .fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(altoPantalla * 0.026F))
                Row(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text(
                            text = "${viewModel.cantidadPosts}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(700),
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                        Text(
                            text = "Posts",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                    }
                    Column(modifier = Modifier.clickable { mostrarLista = "seguidores" }) {
                        Text(
                            text = "${viewModel.seguidores}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(700),
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                        Text(
                            text = "Seguidores",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                    }
                    Column(modifier = Modifier.clickable { mostrarLista = "siguiendo" }) {
                        Text(
                            text = "${viewModel.siguiendo}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(700),
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                        Text(
                            text = "Siguiendo",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                    }
                }
            }
        }
        item {
            HorizontalDivider(color = colorScheme.outline)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Posts",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(700),
                        color = if (mostrando == "Ideas") colorScheme.primary else colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    ),
                    modifier = Modifier.clickable(onClick = { mostrando = "Ideas" }),
                )
                Text(
                    text = "Imagenes",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(700),
                        color = if (mostrando == "Imagenes") colorScheme.primary else colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    ),
                    modifier = Modifier.clickable(onClick = { mostrando = "Imagenes" }),
                )
                Text(
                    text = "Likes",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(700),
                        color = if (mostrando == "Likes") colorScheme.primary else colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    ),
                    modifier = Modifier.clickable(onClick = {
                        mostrando = "Likes"
                        notisViewModel.cargarLikeados(usuarioId)
                    }),
                )
            }
            HorizontalDivider(color = colorScheme.outline)
        }



        when (mostrando) {
            "Imagenes" -> {
                if (viewModel.postsImagenes.isEmpty()) {
                    item {
                        SinPublicaciones(onSubirClick = { onItemClick("subir") }, esMiPerfil = esMiPerfil)
                    }
                } else {
                    galeria(
                        posts = viewModel.postsImagenes,
                        seleccionados = viewModel.seleccionados,
                        onPostClick = onPostClick,
                        onLongClick = { viewModel.imagenAbierta = it  }
                    )
                }
            }

            "Ideas" -> {
                if (viewModel.postsNormales.isEmpty()) {
                    item {
                        SinPublicaciones(onSubirClick = { onItemClick("subir") }, esMiPerfil = esMiPerfil)
                    }
                } else {
                    postFormato(
                        posts = postsFiltrados,
                        ordenActual = ordenActual,
                        onOrdenChange = { ordenActual = it },
                        onLike = { postId ->
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@postFormato
                            val yaLikeo = userId in (postsFiltrados.find { it.id == postId }?.likedBy ?: emptyList())
                            notisViewModel.darLike(postId)
                            viewModel.refrescarPostsLike(postId, userId, !yaLikeo)
                        },
                        onEliminar = { viewModel.eliminarPost(it) },
                        onItemClick = onItemClick,
                        onPostClick = onPostClick,
                        onPerfilClick = onPerfilClick,
                        onSeguirClick = { notisViewModel.toggleSeguirPost(it) },
                        siguiendoMap = notisViewModel.siguiendoMap,
                        botonesfiltrado = true,
                        onComentarClick = onComentarClick
                    )
                }
            }
            "Likes" -> {
                if (notisViewModel.postsLikeados.isEmpty()) {
                    item { SinPublicaciones(onSubirClick = { onItemClick("subir") }, esMiPerfil = esMiPerfil)}
                } else {
                    postFormato(
                        posts = likesFiltrados,
                        ordenActual = ordenActual,
                        onOrdenChange = { ordenActual = it },
                        onLike = { postId ->
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@postFormato
                            val yaLikeo = userId in (postsFiltrados.find { it.id == postId }?.likedBy ?: emptyList())
                            notisViewModel.darLike(postId)
                            viewModel.refrescarPostsLike(postId, userId, !yaLikeo)
                        },
                        onEliminar = { viewModel.eliminarPost(it) },
                        onItemClick = onItemClick,
                        onPostClick = onPostClick,
                        onPerfilClick = onPerfilClick,
                        onSeguirClick = { notisViewModel.toggleSeguirPost(it) },
                        siguiendoMap = notisViewModel.siguiendoMap,
                        botonesfiltrado = true,
                        onComentarClick = onComentarClick
                    )
                }
            }
        }
    }
    if (viewModel.imagenAbierta != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.surface)
                .clickable { viewModel.imagenAbierta = null },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = viewModel.imagenAbierta!!.imagenUrl,
                    contentDescription = viewModel.imagenAbierta!!.texto,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = viewModel.imagenAbierta!!.texto,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = colorScheme.background,
                            fontFamily = FontFamily(Font(R.font.inter_regular))
                        )
                    )
                }
            }
        }
    }
    if (mostrarEditor) {
        EditarPerfilDialog(
            viewModel = viewModel,
            onDismiss = { mostrarEditor = false }
        )
    }
    if (mostrarLista != null) {
        UsuariosListaSheet(
            tipo = mostrarLista!!,
            usuarioId = usuarioId ?: FirebaseAuth.getInstance().currentUser?.uid ?: "",
            onDismiss = { mostrarLista = null },
            onPerfilClick = { uid ->
                mostrarLista = null
                onPerfilClick(uid)
            }
        )
    }
}

@Composable
fun SinPublicaciones(
    onSubirClick: () -> Unit,
    esMiPerfil: Boolean = true
) {
    val altoPantalla = Dimensiones.altoPantalla()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = (altoPantalla * 0.15F)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vacío...",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily(Font(R.font.inter_regular))
                )
            )
            Spacer(modifier = Modifier.height(30.dp))
            if (esMiPerfil) {
                BotonAnimado(
                    "¡Sube algo interesante!",
                    colorScheme.primary,
                    borderColor = colorScheme.primary,
                    backgroundColor = colorScheme.background,
                    onClick = onSubirClick
                )
            }
        }
    }
}