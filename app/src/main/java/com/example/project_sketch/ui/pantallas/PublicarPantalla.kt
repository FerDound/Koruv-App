package com.example.project_sketch.ui.pantallas

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_sketch.PublicarViewModel
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.Dimensiones
import com.example.project_sketch.ui.theme.BlancoPuro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun PublicarPantalla(
    postId: String? = null,
    textoInicial: String = "",
    imagenUrlInicial: String? = null,
    viewModel: PublicarViewModel = viewModel(),
    onPublicado: () -> Unit = {},
    onPublicarClick: (() -> Unit) -> Unit = {},
    imagenUri: Uri? = null,
    onQuitarImagen: () -> Unit = {},
) {
    var texto by remember { mutableStateOf(textoInicial) }
    var imagenQuitada by remember { mutableStateOf(false) }
    val altopantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    val contexto = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    var autorFoto by remember { mutableStateOf("") }
    var autorNombre by remember { mutableStateOf("") }
    var autorUser by remember { mutableStateOf("") }

    LaunchedEffect(imagenUri) {
        if (imagenUri != null) {
            imagenQuitada = false
        }
    }

    val imagenMostrar: Any? = when {
        imagenUri != null -> imagenUri
        !imagenQuitada && imagenUrlInicial != null -> imagenUrlInicial
        else -> null
    }

    LaunchedEffect(userId) {
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("users").document(userId).get().await()
        autorFoto = doc.getString("avatarUrl") ?: ""
        autorNombre = doc.getString("nombrepublico") ?: ""
        autorUser = doc.getString("usuario") ?: ""
    }
    val publicarAction = {
        if (postId != null) {
            viewModel.editar(
                postId = postId,
                textoNuevo = texto,
                imagenUri = imagenUri,
                imagenUrlActual = imagenUrlInicial,
                quitarImagen = imagenQuitada,
                contexto = contexto
            )
        } else {
            viewModel.publicar(texto, imagenUri, contexto)
        }
    }

    Column(
        modifier = Modifier
            .background(color = colorScheme.background)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LaunchedEffect(viewModel.publicadoExitoso) {
            if (viewModel.publicadoExitoso) {
                viewModel.resetEstado()
                onPublicado()
            }
        }
        onPublicarClick(publicarAction)
        Box(modifier = Modifier.fillMaxWidth()) {
            if (imagenMostrar != null ) {
                AsyncImage(
                    model = imagenMostrar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(altopantalla * 0.25F)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(altopantalla * 0.10f)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(if (imagenMostrar != null) 10.dp else 0.dp)
                    .padding(end = 18.dp)
            ) {
                if (autorFoto.isNotEmpty()) {
                    AsyncImage(
                        model = autorFoto,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.poordrawnplaceholder),
                        placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.poordrawnplaceholder),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                }
                Column {
                    Text(
                        text = autorNombre,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = if (imagenMostrar != null) BlancoPuro else colorScheme.onBackground,
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@$autorUser",
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = if (imagenMostrar != null) BlancoPuro else colorScheme.onBackground,
                            )
                        )
                    }
                }
            }

            if (imagenMostrar != null) {
                IconButton(
                    onClick = {
                        if (imagenUri != null) {
                            onQuitarImagen()
                        } else {
                            imagenQuitada = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = anchoPantalla * 0.04854F)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Quitar imagen",
                        tint = BlancoPuro,
                        )
                }
            }
        }

        Spacer(Modifier.height(if (imagenMostrar != null) 0.dp else 16.dp))
        TextField(
            value = texto,
            onValueChange = { texto = it },
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = colorScheme.onBackground
            ),
            placeholder = {
                Text(
                    text = "¿Qué quieres compartir con la comunidad?",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorScheme.tertiary,
                    )
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}