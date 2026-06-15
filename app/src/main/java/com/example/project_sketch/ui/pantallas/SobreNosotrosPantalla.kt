@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.project_sketch.ui.pantallas

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.Dimensiones

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SobreNosotrosPantalla() {
    val anchoPantalla = Dimensiones.anchoPantalla()
    val altoPantalla = Dimensiones.altoPantalla()
    val densidadPantalla = Dimensiones.densidadPantalla()
    var urlAbierta by remember { mutableStateOf<String?>(null) }
    var fuentesExpandidas by remember { mutableStateOf(false) }
    val fuentes = listOf(
        Triple(
            "Chema García (2024)",
            "Ventajas y desventajas de las redes sociales",
            "https://www.cursosfemxa.es/blog/ventajas-redes-sociales"
        ),
        Triple(
            "University of Rochester (2025)",
            "The Positives of Social Media for Teens",
            "https://www.urmc.rochester.edu/news/publications/health-matters/the-positives-of-social-media-for-teens-and-how-parents-can-guide-safe-use"
        ),
        Triple(
            "Mayo Clinic (2025)",
            "Los adolescentes y el uso de las redes sociales",
            "https://www.mayoclinic.org/es/healthy-lifestyle/tween-and-teen-health/in-depth/teens-and-social-media-use/art-20474437"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(Modifier.height(50.dp))
                Image(
                    modifier = Modifier.size(anchoPantalla * 0.2282F, altoPantalla * 0.1156F),
                    painter = painterResource(id = R.drawable.logo_koruv),
                    contentDescription = "Logo Koruv",
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(altoPantalla * 0.05997F))
                Text(
                    text = "KORUV",
                    fontSize = TextUnit.Unspecified,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 24.sp,
                        maxFontSize = with(densidadPantalla) { (anchoPantalla * 0.15533f).toSp() },
                        stepSize = 1.sp
                    ),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.josefin_sans_regular)),
                        color = colorScheme.onBackground,
                        letterSpacing = with(densidadPantalla) { (anchoPantalla * 0.0171f).toSp() },
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tu espacio digital seguro para\ninteractuar, compartir y fortalecer\nlazos escolares.",
                    style = TextStyle(
                        fontSize = (anchoPantalla * 0.03883F).value.sp,
                        lineHeight = (altoPantalla * 0.02835F).value.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Image(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .border(1.dp, colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.bina),
                    contentDescription = "Logo Koruv",
                )
                Text(
                    text = "Autores de Koruv, Castillo Navarro Jose Fernando y Diaz Hurtado David",
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        color = colorScheme.onBackground,
                    )
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Koruv es una plataforma digital (red social) la cual aporta un espacio digital seguro y exclusivo a los alumnos para interactuar, compartir contenido y comunicarse entre ellos lo cual facilita la creación de nuesvas amistades, la expreesión de ideas, opiniones y experiencias  dentro del entorno escolar.",
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Justify,
                        )
                    )
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { fuentesExpandidas = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.tag), // o cualquier icono
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Fuentes",
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(600),
                                    fontSize = 13.sp,
                                    color = colorScheme.primary,
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = fuentesExpandidas,
                            onDismissRequest = { fuentesExpandidas = false },
                            modifier = Modifier
                                .background(colorScheme.background)
                                .widthIn(max = 320.dp)
                        ) {
                            fuentes.forEach { (autor, titulo, url) ->
                                DropdownMenuItem(
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = autor,
                                                style = TextStyle(
                                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                    fontWeight = FontWeight(600),
                                                    fontSize = 13.sp,
                                                    color = colorScheme.onBackground,
                                                )
                                            )
                                            Text(
                                                text = titulo,
                                                style = TextStyle(
                                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                    fontWeight = FontWeight(400),
                                                    fontSize = 12.sp,
                                                    color = colorScheme.onSurfaceVariant,
                                                )
                                            )
                                        }
                                    },
                                    onClick = {
                                        fuentesExpandidas = false
                                        urlAbierta = url
                                    },
                                    modifier = Modifier.background(colorScheme.background)
                                )
                                HorizontalDivider(color = colorScheme.outline)
                            }
                        }
                    }
                }
            }

        }
        if (urlAbierta != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = WebViewClient()
                            loadUrl(urlAbierta!!)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFB2C36))
                        .clickable { urlAbierta = null }
                        .size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun SobreNosotrosPantallaPreview() {
    SobreNosotrosPantalla()
}