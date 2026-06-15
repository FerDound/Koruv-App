package com.example.project_sketch.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_sketch.Pregunta
import com.example.project_sketch.R
import com.example.project_sketch.ui.theme.BlancoPuro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.plus
import kotlin.collections.set


@Composable
fun EncuestaPantalla(onTerminar: () -> Unit) {
    val preguntas = remember {
        listOf(
            Pregunta("¿La aplicación te ayudó a interactuar con más compañeros de tu escuela?", listOf("Si", "No")),
            Pregunta("¿Consideras que una aplicación como esta podría ayudarte a interactuar con más compañeros?", listOf("Si", "Talvez" ,"No")),
            Pregunta("¿Crees que esta aplicación puede facilitar la integración de alumnos nuevos?", listOf("Sí", "Parcialmente", "No")),
            Pregunta("¿Usarías esta aplicación para comunicarte con otros estudiantes?", listOf("Sí", "Talvez", "No")),
            Pregunta("¿La aplicación te parece una alternativa útil para conocer personas dentro del plantel?", listOf("Si", "Talvez", "No")),
        )
    }

    var abierta by remember { mutableStateOf("") }
    val respuestas = remember { mutableStateMapOf<Int, String>() }
    val scope = rememberCoroutineScope()
    var enviando by remember { mutableStateOf(false) }
    var errorVisible by remember { mutableStateOf(false) }
    val todasRespondidas = respuestas.size == preguntas.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 20.dp)
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Encuesta de satisfacción",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight(800),
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    color = colorScheme.onBackground
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Por favor responde todas las preguntas antes de continuar.",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    color = colorScheme.onSurfaceVariant
                )
            )
        }

        preguntas.forEachIndexed { index, pregunta ->
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${index + 1}. ${pregunta.texto}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(600),
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            color = colorScheme.onBackground
                        )
                    )
                    pregunta.opciones.forEach { opcion ->
                        val seleccionada = respuestas[index] == opcion
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (seleccionada) colorScheme.primary.copy(alpha = 0.1f)
                                    else colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    if (seleccionada) colorScheme.primary else colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { respuestas[index] = opcion }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = seleccionada,
                                onClick = { respuestas[index] = opcion },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorScheme.primary
                                )
                            )
                            Text(
                                text = opcion,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    color = if (seleccionada) colorScheme.primary else colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            }

        }
        item {
            Text(
                text = "¿Qué mejorarías de la aplicación para ayudar a los estudiantes a socializar más?",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    color = colorScheme.onBackground
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = abierta,
                onValueChange = { abierta = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Escribe tu respuesta... (Opcional)")
                },
                minLines = 3
            )
        }
        item {
            if (errorVisible) {
                Text(
                    text = "Por favor responde todas las preguntas.",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        color = colorScheme.error
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Button(
                onClick = {
                    if (!todasRespondidas) {
                        errorVisible = true
                        return@Button
                    }
                    errorVisible = false
                    enviando = true
                    scope.launch {
                        try {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                            val db = FirebaseFirestore.getInstance()
                            val userDoc = db.collection("users")
                                .document(uid)
                                .get()
                                .await()

                            val usuario = userDoc.getString("usuario") ?: ""
                            val datos = preguntas.mapIndexed { index, pregunta ->
                                pregunta.texto to (respuestas[index] ?: "")
                            }.toMap() + mapOf(
                                "comentario" to abierta,
                                "uid" to uid,
                                "usuario" to usuario,
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("encuestas").add(datos).await()
                            db.collection("users").document(uid)
                                .update("encuestaRespondida", true).await()
                            onTerminar()
                        } catch (_: Exception) {
                            enviando = false
                        }
                    }
                },
                enabled = !enviando,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(if (todasRespondidas) colorScheme.primary else colorScheme.surface)
            ) {
                Text(
                    text = if (enviando) "Enviando..." else "Enviar respuestas",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        fontSize = 15.sp
                    ),
                    color = if (todasRespondidas) BlancoPuro else colorScheme.tertiary
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}