package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sketch.R
import com.example.project_sketch.TemaViewModel

@Composable
fun OpcionesPantalla(
    onCerrarSesion: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    temaViewModel: TemaViewModel = viewModel()
) {
    val temaActual by temaViewModel.tema.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(color = colorScheme.background)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .size(48.dp)
                        .background(color = colorScheme.surface, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.configuracion),
                        contentDescription = "Configuración",
                        tint = Color(0xFF4B5563),
                    )
                }
                Text(
                    text = "Opciones",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(800),
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                )
            }
            HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
        }
        item {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUENTA",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            letterSpacing = 0.6.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = colorScheme.primary,
                        )
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCerrarSesion() }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cerrarsesion),
                        contentDescription = "Cerrar",
                        tint = Color(0xFFFB2C36),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Cerrar sesión",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFB2C36),
                        )
                    )
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PREFERENCIAS",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            letterSpacing = 0.6.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = colorScheme.primary,
                        )
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
                listOf(
                    Triple("Sistema", "sistema", Icons.Outlined.Android),
                    Triple("Oscuro", "oscuro", Icons.Outlined.DarkMode),
                    Triple("Claro", "claro", Icons.Outlined.LightMode),
                ).forEach { (label, valor, icono) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { temaViewModel.setTema(valor) }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icono,
                                contentDescription = label,
                                tint = if (temaActual == valor) colorScheme.primary else colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(600),
                                    color = if (temaActual == valor) colorScheme.primary else colorScheme.onBackground,
                                )
                            )
                        }
                        if (temaActual == valor) {
                            Icon(
                                painter = painterResource(R.drawable.atras),
                                contentDescription = "Seleccionado",
                                tint = colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INFO",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            letterSpacing = 0.6.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = colorScheme.primary,
                        )
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick("sobrenosotros") }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.logo_koruv),
                        contentDescription = "SobreKoruv",
                        tint = colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Sobre koruv",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = colorScheme.onBackground,
                        )
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
fun OpcionesPantallaPreview() {
    OpcionesPantalla()
}