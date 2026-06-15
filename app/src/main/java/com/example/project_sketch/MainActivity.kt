@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.project_sketch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_sketch.ui.login.Dimensiones
import com.example.project_sketch.ui.pantallas.BarraLateralNavegacion
import com.example.project_sketch.ui.pantallas.BuscarPantalla
import com.example.project_sketch.ui.pantallas.ChatsPantalla
import com.example.project_sketch.ui.pantallas.ConversacionPantalla
import com.example.project_sketch.ui.pantallas.KoruvBarraInferior
import com.example.project_sketch.ui.pantallas.KoruvBarraInferiorPublicar
import com.example.project_sketch.ui.pantallas.KoruvBarraSuperior
import com.example.project_sketch.ui.pantallas.KoruvBarraSuperiorChats
import com.example.project_sketch.ui.pantallas.KoruvBarraSuperiorPost
import com.example.project_sketch.ui.pantallas.KoruvBarraSuperiorPublicar
import com.example.project_sketch.ui.pantallas.MainFeedPantalla
import com.example.project_sketch.ui.pantallas.NotificacionesPantalla
import com.example.project_sketch.ui.pantallas.OpcionesPantalla
import com.example.project_sketch.ui.pantallas.PerfilPantalla
import com.example.project_sketch.ui.pantallas.PostAbiertoPantalla
import com.example.project_sketch.ui.pantallas.PublicarPantalla
import com.example.project_sketch.ui.pantallas.SobreNosotrosPantalla
import com.example.project_sketch.ui.theme.Project_sketchTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.animation.core.tween

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val temaViewModel: TemaViewModel = viewModel()
            val tema by temaViewModel.tema.collectAsState()
            val isDark = when (tema) {
                "oscuro" -> true
                "claro" -> false
                else -> isSystemInDarkTheme()
            }

            Project_sketchTheme(darkTheme = isDark) {

                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()
                val rutaActual = currentRoute?.destination?.route
                val context = LocalContext.current
                var drawerAbierto by remember { mutableStateOf(false) }
                var publicarAccion by remember { mutableStateOf<(() -> Unit)?>(null) }
                var imagenUriPublicar by remember { mutableStateOf<Uri?>(null) }
                var postActual by remember { mutableStateOf<Post?>(null) }
                val notisViewModel: NotisViewModel = viewModel()
                val publicarViewModel: PublicarViewModel = viewModel()

                val tabsPrincipales = remember { setOf("inicio", "buscar", "subir", "chats", "perfil") }
                val esTab = rutaActual in tabsPrincipales || rutaActual == null
                val tabs = listOf(
                    "inicio",
                    "buscar",
                    "subir",
                    "chats",
                    "perfil"
                )

                var indiceTabActual by remember {
                    mutableIntStateOf(0)
                }
                var direccion by remember {
                    mutableIntStateOf(1)
                }

                val navegarTab = { route: String ->

                    val nuevoIndice = tabs.indexOf(route)

                    direccion = when {
                        nuevoIndice > indiceTabActual -> 1
                        nuevoIndice < indiceTabActual -> -1
                        else -> direccion
                    }

                    indiceTabActual = nuevoIndice

                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }

                val navegarPerfil = remember(navController) { { userId: String ->
                    direccion = 1
                    val miUid = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != miUid) {
                        navController.navigate("perfil/$userId") {
                            launchSingleTop = true
                            restoreState = false
                        }
                    } else {
                        navController.navigate("perfil") { launchSingleTop = true }
                    }
                }}

                val navegarPost = remember(navController) { { post: Post ->
                    direccion = 1
                    postActual = post
                    navController.navigate("post_abierto/${post.id}")
                }}

                val navegarComentario = remember(navController) { { post: Post ->
                    direccion = 1
                    postActual = post
                    navController.navigate("post_abierto/${post.id}?comentar=true")
                }}

                val onNotifClick = remember(navController) { { notif: Notificacion ->
                    direccion = 1
                    when (notif.tipo) {
                        "like", "comentario" -> {
                            val post = notisViewModel.postsFeed.find { it.id == notif.postId }
                            if (post != null) {
                                direccion = 1
                                postActual = post
                                navController.navigate("post_abierto/${notif.postId}")
                            }
                        }
                        "seguir" -> {
                            direccion = 1
                            navController.navigate("perfil/${notif.remitenteId}")
                        }
                    }
                }}
                val navegarPantalla = { route: String ->
                    direccion = 1
                    navController.navigate(route)
                }

                Scaffold(
                    topBar = {
                        when (rutaActual) {
                            "post_abierto/{postId}?comentar={comentar}" -> {
                                if (postActual != null) KoruvBarraSuperiorPost(
                                    onBackClick = {
                                        direccion = -1
                                        navController.popBackStack()
                                    }
                                )
                            }
                            "subir" -> KoruvBarraSuperiorPublicar(
                                onBackClick = {
                                    direccion = -1
                                    navController.popBackStack()
                                              },
                                onPublicarClick = { publicarAccion?.invoke() },
                                publicando = publicarViewModel.publicando
                            )
                            "editar/{postId}?texto={texto}&imagen={imagen}" -> KoruvBarraSuperiorPublicar(
                                onBackClick = {
                                    direccion = -1
                                    navController.popBackStack()
                                              },
                                onPublicarClick = { publicarAccion?.invoke() }
                            )
                            "conversacion/{chatId}/{nombre}/{otroUid}/{otroFoto}" -> KoruvBarraSuperiorChats(
                                onBackClick = {
                                    direccion = -1
                                    navController.popBackStack()
                                              },
                                otroNombre = currentRoute?.arguments?.getString("nombre") ?: "",
                                otroFoto = currentRoute?.arguments?.getString("otroFoto") ?: "",
                                onPerfilClick = {
                                    val otroUid = currentRoute?.arguments?.getString("otroUid") ?: ""
                                    direccion = 1
                                    navController.navigate("perfil/$otroUid") { launchSingleTop = true }
                                },
                            )
                            else -> KoruvBarraSuperior(
                                notificaciones = notisViewModel.notificaciones,
                                onMarcarVisto = { notisViewModel.marcarVisto(it) },
                                onNotifClick = onNotifClick,
                                onItemClick = { route ->
                                    direccion = 1
                                    navController.navigate(route) { launchSingleTop = true } },
                                onMenuClick = { if (esTab) drawerAbierto = true },
                                onBackClick = {
                                    direccion = -1
                                    if (esTab) drawerAbierto = false
                                    else navController.popBackStack()
                                },
                                regresar = !esTab || drawerAbierto,
                            )
                        }
                    },
                    bottomBar = {
                        when (rutaActual) {
                            "subir", "editar/{postId}?texto={texto}&imagen={imagen}" -> KoruvBarraInferiorPublicar(
                                onImagenSeleccionada = { imagenUriPublicar = it },
                                onFotoTomada = { imagenUriPublicar = it }
                            )
                            "inicio", "perfil", "buscar", "chats" -> KoruvBarraInferior(
                                currentRoute = rutaActual,
                                onItemClick = { route ->
                                    drawerAbierto = false
                                    if (route == "inicio" && rutaActual == "inicio") notisViewModel.cargarFeed()
                                    else navegarTab(route)
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = "inicio",
                            modifier = Modifier.padding(paddingValues),
                            enterTransition = {
                                slideInHorizontally(
                                    animationSpec = tween(250)
                                ) {
                                    if (direccion > 0) it else -it
                                }
                            },

                            exitTransition = {
                                slideOutHorizontally(
                                    animationSpec = tween(250)
                                ) {
                                    if (direccion > 0) -it else it
                                }
                            },

                            popEnterTransition = {
                                slideInHorizontally(
                                    animationSpec = tween(250)
                                ) { -it }
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    animationSpec = tween(250)
                                ) { it }
                            }
                        ) {
                            composable("inicio") {
                                MainFeedPantalla(
                                    viewModel = notisViewModel,
                                    onPostClick = navegarPost,
                                    onItemClick = { navegarTab(it) },
                                    onPerfilClick = navegarPerfil,
                                    onComentarClick = navegarComentario
                                )
                            }
                            composable("perfil") {
                                PerfilPantalla(
                                    notisViewModel = notisViewModel,
                                    onMensajearClick = { chatId, nombre, foto, otroUid ->
                                        direccion = 1
                                        navController.navigate("conversacion/$chatId/$nombre/$otroUid/${Uri.encode(foto)}")
                                    },
                                    onPostClick = navegarPost,
                                    onItemClick = { navegarTab(it) },
                                    onPerfilClick = navegarPerfil,
                                    onComentarClick = navegarComentario
                                )
                            }
                            composable("buscar") {
                                BuscarPantalla(
                                    notisViewModel = notisViewModel,
                                    onItemClick = { navegarPantalla(it) },
                                    onPerfilClick = {
                                        direccion = 1
                                        navController.navigate("perfil/$it")
                                    },
                                    onPostClick = navegarPost,
                                    onComentarClick = navegarComentario
                                )
                            }
                            composable("chats") {
                                ChatsPantalla(
                                    onChatClick = { chatId, nombre, foto, otroUid ->
                                        direccion = 1
                                        navController.navigate("conversacion/$chatId/$nombre/$otroUid/${Uri.encode(foto)}")
                                    }
                                )
                            }
                            composable("notis") {
                                NotificacionesPantalla(
                                    viewModel = notisViewModel,
                                    onNotifClick = onNotifClick
                                )
                            }
                            composable(
                                "post_abierto/{postId}?comentar={comentar}",
                                arguments = listOf(
                                    navArgument("postId") { defaultValue = "" },
                                    navArgument("comentar") { defaultValue = "false" }
                                )
                            ) { back ->
                                val comentar = back.arguments?.getString("comentar") == "true"
                                postActual?.let { post ->
                                    PostAbiertoPantalla(
                                        post = post,
                                        posts = notisViewModel.postsFeed,
                                        viewModel = notisViewModel,
                                        onItemClick = { navegarPantalla(it) },
                                        onEliminar = { navController.popBackStack() },
                                        abrirTeclado = comentar,
                                        siguiendoMap = notisViewModel.siguiendoMap,
                                        onSeguirClick = { notisViewModel.toggleSeguirPost(it) },
                                        onPerfilClick = navegarPerfil
                                    )
                                }
                            }
                            composable("subir") {
                                val perfilViewModel: PerfilViewModel = viewModel()
                                PublicarPantalla(
                                    viewModel = publicarViewModel,
                                    imagenUri = imagenUriPublicar,
                                    onQuitarImagen = { imagenUriPublicar = null },
                                    onPublicado = {
                                        imagenUriPublicar = null
                                        perfilViewModel.recargarPosts()
                                        notisViewModel.cargarFeed()
                                        navController.popBackStack()
                                    },
                                    onPublicarClick = { publicarAccion = it }
                                )
                            }
                            composable("editar/{postId}?texto={texto}&imagen={imagen}") { backStackEntry ->
                                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                                val texto = backStackEntry.arguments?.getString("texto") ?: ""
                                val imagen = Uri.decode(backStackEntry.arguments?.getString("imagen") ?: "")
                                val perfilViewModel: PerfilViewModel = viewModel()
                                PublicarPantalla(
                                    postId = postId,
                                    textoInicial = texto,
                                    imagenUrlInicial = if (imagen.isBlank() || imagen == "null") null else imagen,
                                    imagenUri = imagenUriPublicar,
                                    onQuitarImagen = { imagenUriPublicar = null },
                                    onPublicado = {
                                        imagenUriPublicar = null
                                        perfilViewModel.recargarPosts()
                                        notisViewModel.cargarFeed()
                                        navController.popBackStack()
                                    },
                                    onPublicarClick = { publicarAccion = it }
                                )
                            }
                            composable("buscar_feed/{orden}") { backStackEntry ->
                                val orden = backStackEntry.arguments?.getString("orden") ?: "nuevos"
                                MainFeedPantalla(
                                    viewModel = notisViewModel,
                                    ordenInicial = orden,
                                    onPostClick = navegarPost,
                                    onItemClick = { navegarPantalla(it) },
                                    onPerfilClick = navegarPerfil,
                                    onComentarClick = navegarComentario,
                                    pagina = false,
                                )
                            }
                            composable("conversacion/{chatId}/{nombre}/{otroUid}/{otroFoto}") { back ->
                                ConversacionPantalla(
                                    chatId = back.arguments?.getString("chatId") ?: return@composable,
                                    otroNombre = back.arguments?.getString("nombre") ?: "",
                                    otroUid = back.arguments?.getString("otroUid") ?: "",
                                    otroFoto = back.arguments?.getString("otroFoto") ?: ""
                                )
                            }
                            composable("perfil/{userId}") { backStackEntry ->
                                val perfilActual = backStackEntry.arguments?.getString("userId") ?: ""
                                val miUid = FirebaseAuth.getInstance().currentUser?.uid
                                PerfilPantalla(
                                    usuarioId = perfilActual,
                                    notisViewModel = notisViewModel,
                                    onMensajearClick = { chatId, nombre, foto, otroUid ->
                                        direccion = 1
                                        navController.navigate("conversacion/$chatId/$nombre/$otroUid/${Uri.encode(foto)}")
                                    },
                                    onPostClick = navegarPost,
                                    onItemClick = { route ->
                                        if (route == "inicio") {
                                            navController.popBackStack("inicio", inclusive = false)
                                        } else navegarTab(route)
                                    },
                                    onPerfilClick = { userIdDestino ->
                                        when (userIdDestino) {
                                            miUid -> navController.navigate("perfil") { launchSingleTop = true }
                                            perfilActual -> {}
                                            else -> {
                                                direccion = 1
                                                navController.navigate("perfil/$userIdDestino") {
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    },
                                    onComentarClick = navegarComentario
                                )
                            }
                            composable("configuracion") {
                                OpcionesPantalla(
                                    onCerrarSesion = {
                                        FirebaseAuth.getInstance().signOut()
                                        context.startActivity(Intent(context, LoginActivity::class.java))
                                        (context as ComponentActivity).finish()
                                    },
                                    onItemClick = { navegarPantalla(it) },
                                    temaViewModel = temaViewModel
                                )
                            }
                            composable("sobrenosotros") { SobreNosotrosPantalla() }
                        }

                        // Drawer
                        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                            AnimatedVisibility(visible = drawerAbierto, enter = fadeIn(), exit = fadeOut()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .clickable { drawerAbierto = false }
                                )
                            }
                            AnimatedVisibility(
                                visible = drawerAbierto,
                                enter = slideInHorizontally { -it },
                                exit = slideOutHorizontally { -it }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(colorScheme.background)
                                        .width(Dimensiones.anchoPantalla() * 0.9f)
                                        .fillMaxHeight()
                                        .clickable(enabled = false) {}
                                ) {
                                    BarraLateralNavegacion(
                                        onItemClick = { route ->
                                            drawerAbierto = false
                                            direccion = 1
                                            navController.navigate(route)
                                        },
                                        onOrdenClick = { orden ->
                                            drawerAbierto = false
                                            direccion = 1
                                            navController.navigate("buscar_feed/$orden") {
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}