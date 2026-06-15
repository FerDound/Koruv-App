package com.example.project_sketch.ui.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_sketch.PerfilViewModel
import com.example.project_sketch.R

@Composable
fun EditarPerfilDialog(
    viewModel: PerfilViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf(viewModel.nombrepublico) }
    var usuario by remember { mutableStateOf(viewModel.usuario) }
    var sinFoto by remember { mutableStateOf(false) }
    var nuevaFotoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        nuevaFotoUri = uri
        sinFoto = false
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.background,
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Cerrar",
                        tint = colorScheme.onBackground
                    )
                }
                Text(
                    text = "Editar perfil",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Guardar",
                    color = colorScheme.background,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = colorScheme.background
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 50.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            colorScheme.primary
                        )
                        .border(1.dp, colorScheme.primary, RoundedCornerShape(20.dp))
                        .clickable {
                            viewModel.actualizarPerfil(
                                nombrePublico = nombre,
                                usuario = usuario
                            )
                            when {
                                sinFoto -> {
                                    viewModel.quitarFotoPerfil()
                                }

                                nuevaFotoUri != null -> {
                                    viewModel.subirFotoPerfil(
                                        context,
                                        nuevaFotoUri!!
                                    )
                                }
                            }
                            onDismiss()
                        }
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 24.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            sinFoto -> {
                                Image(
                                    painter = painterResource(R.drawable.poordrawnplaceholder),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                )
                            }

                            nuevaFotoUri != null -> {
                                AsyncImage(
                                    model = nuevaFotoUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                )
                            }

                            !viewModel.fotoPerfil.isNullOrEmpty() -> {
                                AsyncImage(
                                    model = viewModel.fotoPerfil,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                )
                            }

                            else -> {
                                Image(
                                    painter = painterResource(R.drawable.poordrawnplaceholder),
                                    contentDescription = null
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(colorScheme.onBackground.copy(alpha = 0.2f))
                                .clickable {
                                    launcher.launch("image/*")
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.camara),
                                contentDescription = "Cámara",
                                tint = colorScheme.background,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }
                    Text(
                        "Quitar foto",
                        color = colorScheme.onBackground,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = colorScheme.background
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(colorScheme.background)
                            .border(1.dp, colorScheme.primary, RoundedCornerShape(20.dp))
                            .clickable {
                                sinFoto = true
                                nuevaFotoUri = null
                            }
                            .padding(top = 8.dp, bottom = 8.dp)
                            .width(80.dp),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.padding(top = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Nombre público",
                            fontWeight = FontWeight.SemiBold
                        )

                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            singleLine = true
                        )

                        Text(
                            text = "Usuario",
                            fontWeight = FontWeight.SemiBold
                        )

                        TextField(
                            value = usuario,
                            onValueChange = { usuario = it },
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
