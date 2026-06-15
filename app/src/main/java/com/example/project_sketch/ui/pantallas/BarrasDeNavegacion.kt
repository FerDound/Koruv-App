package com.example.project_sketch.ui.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.project_sketch.Notificacion
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.Dimensiones
import com.example.project_sketch.ui.login.fechaFormateada
import com.example.project_sketch.ui.theme.BlancoPuro

@Composable
fun KoruvBarraSuperior(
    onMenuClick: () -> Unit = {},
    onItemClick: (String) -> Unit,
    notificaciones: List<Notificacion> = emptyList(),
    onMarcarVisto: (String) -> Unit = {},
    onNotifClick: (Notificacion) -> Unit = {},
    regresar: Boolean = false,
    onBackClick: () -> Unit
) {
    var menuNotifs by remember { mutableStateOf(false) }
    val sinVer = notificaciones.count { !it.visto }
    val noVistas = notificaciones.filter { !it.visto }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(50.dp)
            .background(colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = if (regresar) onBackClick else onMenuClick,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Icon(painter = if(regresar) painterResource(R.drawable.atras) else painterResource(R.drawable.menu), contentDescription = "Menu")
        }
        Text(
            text = "Koruv",
            style = TextStyle(
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontFamily = FontFamily(Font(R.font.josefin_sans_regular)),
                fontWeight = FontWeight(600),
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        )
        Box {
            IconButton(
                onClick = { menuNotifs = true },
                modifier = Modifier.padding(end = 12.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (sinVer > 0) Badge(containerColor = colorScheme.secondary, contentColor = BlancoPuro) { Text("$sinVer" ) }
                    }
                ) {
                    Icon(painter = painterResource(R.drawable.notif), contentDescription = "notificacion")
                }
            }

            DropdownMenu(
                modifier = Modifier
                    .width(320.dp)
                    .background(colorScheme.background, RoundedCornerShape(16.dp))
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp),
                expanded = menuNotifs,
                onDismissRequest = { menuNotifs = false },
            ) {
                if (noVistas.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Sin notificaciones nuevas.",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                )
                            )
                        },
                        onClick = {},
                    )
                } else {
                    noVistas.take(5).forEach { notif ->
                        val texto = when (notif.tipo) {
                            "like" -> "${notif.remitenteNombre} le dio like a tu publicación."
                            "comentario" -> "${notif.remitenteNombre} comentó tu publicación."
                            "seguir" -> "${notif.remitenteNombre} empezó a seguirte."
                            else -> ""
                        }
                        DropdownMenuItem(
                            modifier = Modifier.background(
                                if (!notif.visto) colorScheme.background else colorScheme.primary.copy(alpha = 0.12f),
                                RoundedCornerShape(16.dp)
                            ),
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = notif.remitenteFoto,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        error = painterResource(R.drawable.poordrawnplaceholder),
                                        placeholder = painterResource(R.drawable.poordrawnplaceholder),
                                        modifier = Modifier.size(36.dp).clip(CircleShape)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = texto,
                                            style = TextStyle(
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight(600),
                                                color = colorScheme.onBackground,
                                                fontFamily = FontFamily(Font(R.font.inter_regular))
                                            )
                                        )
                                        Text(
                                            text = fechaFormateada(notif.timestamp),
                                            style = TextStyle(
                                                fontSize = 11.sp,
                                                color = colorScheme.tertiary,
                                                fontFamily = FontFamily(Font(R.font.inter_regular))
                                            )
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onMarcarVisto(notif.id)
                                onNotifClick(notif)
                                menuNotifs = false
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            "Ver todas",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight(600),
                                color = colorScheme.primary,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        menuNotifs = false
                        onItemClick("notis")
                    }
                )
            }
        }
    }
}
@Composable
fun KoruvBarraSuperiorChats(
    onBackClick: () -> Unit = {},
    otroNombre: String = "",
    otroFoto: String = "",
    onPerfilClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(50.dp)
            .background(colorScheme.background)
            .padding(end = 16.dp, start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.atras),
                    contentDescription = "Regresar"
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onPerfilClick() }
            ) {
                if (otroFoto.isNotEmpty()) {
                    AsyncImage(
                        model = otroFoto,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.poordrawnplaceholder),
                        placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.poordrawnplaceholder),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = otroNombre,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = colorScheme.onBackground
                        )
                    )
                }
            }
        }
    }
}
@Composable
fun KoruvBarraSuperiorPost(
    onBackClick: () -> Unit = {},
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(50.dp)
            .background(colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Regresar"
                )
            }

            Text(
                text = "Publicación",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.josefin_sans_regular)),
                    fontWeight = FontWeight(600),
                    color = colorScheme.onBackground,
                )
            )
        }


    }
}

@Composable
fun KoruvBarraSuperiorPublicar(
    onBackClick: () -> Unit = {},
    onPublicarClick: () -> Unit = {},
    publicando: Boolean = false  // 👈
) {
    val anchoPantalla = Dimensiones.anchoPantalla()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(50.dp)
            .background(colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .height(24.dp)
                .padding(start = anchoPantalla * 0.04854F)
        ) {
            Icon(Icons.Outlined.Close, contentDescription = "Regresar")
        }
        Text(
            text = "Crear publicación",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(600),
                color = colorScheme.onBackground,
            )
        )
        Button(
            onClick = onPublicarClick,
            enabled = !publicando,  // 👈
            modifier = Modifier.padding(end = anchoPantalla * 0.04854F),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (publicando) colorScheme.tertiary else colorScheme.primary  // 👈
            )
        ) {
            if (publicando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = colorScheme.background,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Publicar",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(500),
                        color = colorScheme.background,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}


@Composable
fun KoruvBarraInferiorPublicar(
    onImagenSeleccionada: (Uri) -> Unit = {},
    onFotoTomada: (Uri) -> Unit = {}
) {
    val contexto = LocalContext.current
    val anchoPantalla = Dimensiones.anchoPantalla()

    val fotoUri = remember {
        val archivo = java.io.File(contexto.cacheDir, "foto_temp.jpg")
        FileProvider.getUriForFile(contexto, "${contexto.packageName}.provider", archivo)
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) onFotoTomada(fotoUri)
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) camaraLauncher.launch(fotoUri)
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImagenSeleccionada(it) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(45.dp)
            .background(colorScheme.background)
            .padding(start = anchoPantalla * 0.04854F, end = anchoPantalla * 0.04854F),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(anchoPantalla * 0.07766F)
    ) {
        IconButton(
            onClick = { galeriaLauncher.launch("image/*") },
            modifier = Modifier.height(24.dp).width(24.dp)
        ) {
            Icon(painter = painterResource(R.drawable.imagenes), contentDescription = "Imagenes", tint = colorScheme.primary)
        }
        IconButton(
            onClick = { permisoCamara.launch(android.Manifest.permission.CAMERA) },
            modifier = Modifier.height(24.dp).width(24.dp)
        ) {
            Icon(painter = painterResource(R.drawable.camara), contentDescription = "Foto", tint = colorScheme.primary)
        }
    }
}


@Composable
fun KoruvBarraInferior(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val anchoPantalla = Dimensiones.anchoPantalla()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(80.dp)
            .background(colorScheme.background)
            .padding(start = anchoPantalla * 0.04854F, end = anchoPantalla * 0.04854F),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onItemClick("inicio") },
            Modifier
                .padding(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.feed), contentDescription = "Inicio", tint = if (currentRoute == "inicio") colorScheme.primary else  colorScheme.tertiary)
        }
        IconButton(
            onClick = { onItemClick("buscar") },
            Modifier
                .padding(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.buscar), contentDescription = "Buscar", tint = if (currentRoute == "buscar") colorScheme.primary else  colorScheme.tertiary)
        }
        IconButton(
            onClick = { onItemClick("subir") },
            modifier = Modifier
                .padding(8.dp)
                .background(
                            color = colorScheme.secondary,
                            shape = CircleShape
                )
        ) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = "Menu",
                tint = colorScheme.background
            )
        }
        IconButton(
            onClick = { onItemClick("chats") },
            Modifier
                .padding(8.dp),
        ) {
            Icon(painter = painterResource(R.drawable.mensaje), contentDescription = "Chat", tint = if (currentRoute == "chats") colorScheme.primary else  colorScheme.tertiary)
        }
        IconButton(
            onClick = { onItemClick("perfil") },
            Modifier
                .padding(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.perfil), contentDescription = "Perfil", tint = if (currentRoute == "perfil") colorScheme.primary else  colorScheme.tertiary)
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
fun PreviewBarras() {
    Scaffold(
        topBar = { KoruvBarraSuperiorPublicar() },
        bottomBar = { KoruvBarraInferiorPublicar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = colorScheme.background)
        )
    }
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun PreviewBarras2() {
    Scaffold(
        topBar = { KoruvBarraSuperiorPublicar() },
        bottomBar = { KoruvBarraInferior( currentRoute = "inicio", onItemClick = {}) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = colorScheme.background)
        )
    }
}