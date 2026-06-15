package com.example.project_sketch.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.project_sketch.R
import com.example.project_sketch.ui.theme.BlancoPuro

val TEXTO_TERMINOS = """
Términos de Uso y Política de Privacidad de Koruv

Última actualización: junio 2026

1. ACEPTACIÓN DE LOS TÉRMINOS
Al usar Koruv, aceptas estos términos. Si no estás de acuerdo, no uses la aplicación.

2. USO DE LA APLICACIÓN
Koruv es una red social escolar diseñada para facilitar la comunicación, interacción e integración entre estudiantes.

3. CONTENIDO
Eres responsable del contenido que publicas. No se permite contenido ofensivo, ilegal o que viole los derechos de terceros.

4. PRIVACIDAD
Recopilamos información básica de tu cuenta (nombre, correo, carrera) para personalizar tu experiencia. No compartimos ni vendemos información personal a terceros con fines comerciales.

5. DATOS PERSONALES
Tu información se almacena en servidores seguros. Puedes solicitar la eliminación de tu cuenta y datos en cualquier momento.

6. MODIFICACIONES
Podemos actualizar estos términos. Te notificaremos de cambios importantes a través de la aplicación.

7. CONTACTO
Si tienes preguntas, contáctanos a través de la aplicación o por correo electrónico a josefernandocastillonavarro@gmail.com.

8. USO DE LA APLICACIÓN
Koruv está dirigida a estudiantes de la institución educativa para la que fue desarrollada. Al utilizar la aplicación, confirmas que formas parte de la comunidad estudiantil correspondiente.

9. MODERACIÓN
Nos reservamos el derecho de eliminar publicaciones o cuentas que incumplan estos términos o afecten la convivencia dentro de la comunidad.

10. RESPONSABILIDAD
Koruv se proporciona con fines académicos y de comunicación entre estudiantes. No garantizamos la disponibilidad continua del servicio ni nos hacemos responsables de las opiniones o contenidos publicados por los usuarios.
""".trimIndent()

@Composable
fun TerminosModal(
    onAceptar: () -> Unit,
    onCerrar: () -> Unit
) {
    val listState = rememberLazyListState()
    val scrollLlegoAlFinal by remember {
        derivedStateOf { !listState.canScrollForward }
    }
    var llegoAlFinal by remember { mutableStateOf(false) }

    LaunchedEffect(scrollLlegoAlFinal) {
        if (scrollLlegoAlFinal) {
            kotlinx.coroutines.delay(300)
            llegoAlFinal = true
        }
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Términos y Privacidad",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = colorScheme.onSurface
                        )
                    )
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.Outlined.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider()

                // Texto scrolleable
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    item {
                        Text(
                            text = TEXTO_TERMINOS,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = colorScheme.onSurface
                            )
                        )
                    }
                }

                HorizontalDivider()

                // Botón aceptar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!llegoAlFinal) {
                        Text(
                            text = "Desplázate hasta el final para aceptar",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    } else {
                        BotonAnimado(
                            text = "Aceptar términos",
                            textColor = BlancoPuro,
                            backgroundColor = colorScheme.primary,
                            onClick = onAceptar
                        )
                    }
                }
            }
        }
    }
}