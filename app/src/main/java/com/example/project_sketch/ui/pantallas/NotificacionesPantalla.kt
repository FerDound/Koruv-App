package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_sketch.Notificacion
import com.example.project_sketch.NotisViewModel
import com.example.project_sketch.R
import com.example.project_sketch.ui.login.fechaFormateada

@Composable
fun NotificacionesPantalla(
    viewModel: NotisViewModel = viewModel(),
    onNotifClick: (Notificacion) -> Unit = {}
) {
    LazyColumn(
        Modifier.fillMaxWidth().background(colorScheme.background)
    ) {
        if (viewModel.notificaciones.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin notificaciones",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily(Font(R.font.inter_regular))
                        )
                    )
                }
            }
        } else {
            notifs(
                notificaciones = viewModel.notificaciones,
                onVisto = { viewModel.marcarVisto(it) },
                onNotifClick = onNotifClick
            )
        }
    }
}

fun LazyListScope.notifs(
    notificaciones: List<Notificacion>,
    onVisto: (String) -> Unit = {},
    onNotifClick: (Notificacion) -> Unit = {}
) {
    items(notificaciones) { notif ->
        val texto = when (notif.tipo) {
            "like" -> "${notif.remitenteNombre} le dio like a tu publicación"
            "comentario" -> "${notif.remitenteNombre} comentó tu publicación"
            "seguir" -> "${notif.remitenteNombre} empezó a seguirte"
            else -> ""
        }

        HorizontalDivider(color = colorScheme.outline, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (!notif.visto) colorScheme.background else Color(0x30DBE7FB))
                .clickable {
                    if (!notif.visto)
                        onVisto(notif.id)
                    onNotifClick(notif)
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = notif.remitenteFoto,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.poordrawnplaceholder),
                placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = texto,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(600),
                        color = colorScheme.onBackground,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    )
                )
                Text(
                    text = fechaFormateada(notif.timestamp),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorScheme.tertiary,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    )
                )
            }
        }
    }

}

@Preview(widthDp = 412, heightDp = 917)
@Composable
fun NotificacionesPantallaPreview() {
    NotificacionesPantalla()
}