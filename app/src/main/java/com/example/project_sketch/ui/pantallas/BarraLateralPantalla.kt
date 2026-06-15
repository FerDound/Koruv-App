package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.BuscarObjetos
import com.example.project_sketch.ui.login.Dimensiones

@Composable
fun BarraLateralNavegacion(
    onItemClick: (String) -> Unit,
    onOrdenClick: (String) -> Unit,
) {
    BarraLateralPantalla(
        onItemClick = onItemClick,
        onOrdenClick = onOrdenClick
    )
}

@Composable
fun BarraLateralPantalla(
    onItemClick: (String) -> Unit,
    onOrdenClick: (String) -> Unit,
) {
    val anchopantalla = Dimensiones.anchoPantalla()

    LazyColumn(
            modifier = Modifier
                .width(anchopantalla*0.9F)
                .background(color = colorScheme.background)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(start = 16.dp, end = 22.dp, top = 22.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DESCUBRIR",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(700),
                        color = colorScheme.onBackground,
                        letterSpacing = 0.7.sp,
                    )
                )
            }
            BuscarObjetos("Popular", painterResource(R.drawable.popular), "Lo más comentado en la comunidad", colorScheme.secondary, Color(0x80FFB5A0), 0, onObjetoClick = { onOrdenClick("likes")} )
            BuscarObjetos("Reciente", painterResource(R.drawable.reciente), "Últimas publicaciones", colorScheme.primary, Color(0x8090CAF9), 0, onObjetoClick = { onOrdenClick("nuevos") } )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(start = 16.dp, end = 22.dp, top = 22.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONECTAR",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(700),
                        color = colorScheme.onBackground,
                        letterSpacing = 0.7.sp,
                    )
                )
            }
            BuscarObjetos("Seguidos", painterResource(R.drawable.amigos), "¿Que subio la gente que sigues?", notif = 0, coloricono = Color(0xFF4B5563), onObjetoClick = { onOrdenClick("seguidos")})
            BuscarObjetos("Amigos", painterResource(R.drawable.calendario), "Ver actualizaciones de tu círculo", notif = 0, coloricono = Color(0xFF4B5563), onObjetoClick = { onOrdenClick("amigos")})
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(start = 16.dp, end = 22.dp, top = 22.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONFIGURACIÓN",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(700),
                        color = colorScheme.onBackground,
                        letterSpacing = 0.7.sp,
                    )
                )
            }
            BuscarObjetos("Opciones", painterResource(R.drawable.configuracion), "Ver actualizaciones de tu círculo", notif = 0, coloricono = Color(0xFF4B5563), onObjetoClick = { onItemClick("configuracion")})
        }
    }
}
