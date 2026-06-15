package com.example.project_sketch.ui.login

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_sketch.Comentario
import com.example.project_sketch.Post
import com.example.project_sketch.R
import com.example.project_sketch.ui.theme.AzulKoruv
import com.example.project_sketch.ui.theme.BlancoPuro
import com.example.project_sketch.ui.theme.BordeOscuro
import com.example.project_sketch.ui.theme.TextoTercioClaro
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


//
//Dimensiones
//
object Dimensiones {
    @Composable
    fun altoPantalla(): Dp {
        val heightPx = LocalWindowInfo.current.containerSize.height
        return with(LocalDensity.current) { heightPx.toDp() }
    }

    @Composable
    fun densidadPantalla() = LocalDensity.current

    @Composable
    fun anchoPantalla(): Dp {
        val widthPx = LocalWindowInfo.current.containerSize.width
        return with(LocalDensity.current) { widthPx.toDp() }
    }
}

//
//CAMPOS DE DATOS
//
@Composable
fun CampoDeDatos(
    texto: String,
    valor: String,
    onValorChange: (String) -> Unit,
    icon: ImageVector? = null,
    password: Boolean = false,
    iconPainter: Painter? = null,
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    var visible by remember { mutableStateOf(!password) }

    Row(
        modifier = Modifier
            .border(1.dp, color = colorScheme.outline, RoundedCornerShape(altoPantalla * 0.01308F))
            .fillMaxWidth()
            .height(altoPantalla * 0.05452F)
            .background(color = colorScheme.surface, RoundedCornerShape(altoPantalla * 0.01308F))
            .padding(horizontal = anchoPantalla * 0.03883F, vertical = altoPantalla * 0.01308F),
        horizontalArrangement = Arrangement.spacedBy(anchoPantalla * 0.02F),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = TextoTercioClaro
            )
        } else if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = TextoTercioClaro
            )
        }

        BasicTextField(
            value = valor,
            onValueChange = onValorChange,
            visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (password) KeyboardType.Password else KeyboardType.Text
            ),
            textStyle = TextStyle(
                fontSize = (anchoPantalla*0.03398F).value.sp,
                lineHeight = (altoPantalla*0.02181F).value.sp,
                color = colorScheme.onSurface,
                fontFamily = FontFamily(Font(R.font.inter_regular))
            ),
            decorationBox = { innerTextField ->
                if (valor.isEmpty()) {
                    Text(
                        text = texto,
                        style = TextStyle(
                            fontSize = (anchoPantalla*0.03398F).value.sp,
                            lineHeight = (altoPantalla*0.02181F).value.sp,
                            color = TextoTercioClaro,
                            fontFamily = FontFamily(Font(R.font.inter_regular))
                        )
                    )
                }
                innerTextField()
            }
        )

        if (password) {
            IconButton(
                onClick = { visible = !visible }
            ) {
                Icon(
                    if (visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (visible) "Ocultar" else "Mostrar",
                    tint = TextoTercioClaro
                )
            }
        }
    }
}

//
//CamposDropdown
//
@Composable
fun CampoDropdown(
    text: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    var expandido by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .border(1.dp, color = colorScheme.outline, RoundedCornerShape(altoPantalla * 0.01308F))
                .fillMaxWidth()
                .height(altoPantalla * 0.05452F)
                .background(color = colorScheme.surface, RoundedCornerShape(altoPantalla * 0.01308F))
                .clickable { expandido = true }
                .padding(horizontal = anchoPantalla * 0.03883F, vertical = altoPantalla * 0.01308F),
            horizontalArrangement = Arrangement.spacedBy(anchoPantalla * 0.02F),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = TextoTercioClaro)
            } else if (iconPainter != null) {
                Icon(painter = iconPainter, contentDescription = null, tint = TextoTercioClaro)
            }
            Text(
                text = valor.ifBlank { text },
                style = TextStyle(
                    fontSize = (anchoPantalla * 0.03398F).value.sp,
                    lineHeight = (altoPantalla * 0.02181F).value.sp,
                    color = if (valor.isBlank()) TextoTercioClaro else colorScheme.onSurface,
                    fontFamily = FontFamily(Font(R.font.inter_regular))
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = TextoTercioClaro
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = { onSeleccion(opcion); expandido = false }
                )
            }
        }
    }
}
//
//Tags
//
@Composable
fun Etiquetas(
    texto: String,
    colores: Color,
) {
    Row(
        Modifier
            .height(20.dp)
            .background(color = colores, shape = RoundedCornerShape(size = 8.dp))
            .padding(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 2.dp)
    ) {
        Text(
            text = texto,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = BlancoPuro,
            )
        )
    }
}
//
//Boton Animado
//
@Composable
fun BotonAnimado(
    text: String,
    textColor: Color,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()

    var pressed by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.Gray
            pressed && backgroundColor != Color.Transparent -> Color.DarkGray
            else -> backgroundColor
        },
        label = "buttonColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        label = "buttonScale"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(120)
            onClick()
            pressed = false
        }
    }

    val shape = RoundedCornerShape(altoPantalla * 0.01308F)
    val borderModifier = if (borderColor != null)
        Modifier.border(1.dp, borderColor, shape)
    else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(animatedColor, shape)
            .then(borderModifier)
            .clickable(enabled = enabled) { pressed = true }
            .padding(
                horizontal = anchoPantalla * 0.03883F,
                vertical = altoPantalla * 0.01744F
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            maxLines = 1,
            fontSize = TextUnit.Unspecified,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = with(Dimensiones.densidadPantalla()) { (anchoPantalla * 0.03883F).toSp() },
                stepSize = 1.sp
            ),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(600),
                color = if (enabled) textColor else colorScheme.background,
                textAlign = TextAlign.Center,
            )
        )
    }
}
//
//Boton de filtrado perfil
//
@Composable
fun FiltroBoton(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val borderColor = if (seleccionado) colorScheme.primary else colorScheme.outline
    val textColor = if (seleccionado) colorScheme.background else colorScheme.onSurfaceVariant
    val fondoColor = if (seleccionado) colorScheme.primary else Color.Transparent

    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(fondoColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(500),
                color = textColor
            )
        )
    }
}
//
//Posts formato
//
fun LazyListScope.postFormato(
    posts: List<Post>,
    ordenActual: String,
    onOrdenChange: (String) -> Unit,
    onLike: (String) -> Unit,
    onEliminar: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onPostClick: (Post) -> Unit = {},
    onPerfilClick: (String) -> Unit = {},
    onSeguirClick: (String) -> Unit = {},
    siguiendoMap: Map<String, Boolean> = emptyMap(),
    onComentarClick: (Post) -> Unit = {},
    botonesfiltrado: Boolean = false,
) {
    if (botonesfiltrado) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FiltroBoton("Más nuevos", ordenActual == "nuevos") { onOrdenChange("nuevos") }
                FiltroBoton("Más viejos", ordenActual == "viejos") { onOrdenChange("viejos") }
                FiltroBoton("Popular", ordenActual == "likes") { onOrdenChange("likes") }
            }
        }
    }
    items(posts) { post ->
        val contexto = LocalContext.current
        val scope = rememberCoroutineScope()
        var guardadoExitoso by remember { mutableStateOf(false) }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val yaLikeo = userId in post.likedBy
        var menuExpandido by remember { mutableStateOf(false) }
        val esMiPost = post.autorId == userId
        val altopantalla = Dimensiones.altoPantalla()
        val yaSigue = siguiendoMap[post.autorId] ?: false
        val bitmapController = rememberGraphicsLayer()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.background)
                .clickable { onPostClick(post) }
                .drawWithContent {
                    bitmapController.record { this@drawWithContent.drawContent() }
                    drawContent()
                }
        ) {
            if (!post.imagenUrl.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = post.imagenUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(altopantalla * 0.255f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(altopantalla * 0.10f)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AutorRow(post = post, onPerfilClick = onPerfilClick, sobreImagen = true)
                        MenuOpciones(
                            post = post, esMiPost = esMiPost, yaSigue = yaSigue,
                            menuExpandido = menuExpandido, onMenuExpandido = { menuExpandido = it },
                            onItemClick = onItemClick, onEliminar = onEliminar,
                            onSeguirClick = onSeguirClick, sobreImagen = true
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(16.dp)
            ) {
                if (post.imagenUrl.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.height(40.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AutorRow(post = post, onPerfilClick = onPerfilClick)
                        MenuOpciones(
                            post = post, esMiPost = esMiPost, yaSigue = yaSigue,
                            menuExpandido = menuExpandido, onMenuExpandido = { menuExpandido = it },
                            onItemClick = onItemClick, onEliminar = onEliminar,
                            onSeguirClick = onSeguirClick
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = post.texto,
                    style = TextStyle(
                        fontSize = 14.sp, lineHeight = 22.75.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400), color = colorScheme.onBackground,
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Row(
                            modifier = Modifier.padding(end = 24.dp).width(62.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = if (yaLikeo) painterResource(R.drawable.corazonlike) else painterResource(R.drawable.corazon),
                                contentDescription = "Me gusta",
                                tint = if (yaLikeo) colorScheme.secondary else colorScheme.tertiary,
                                modifier = Modifier.size(18.dp).clickable { onLike(post.id) }
                            )
                            Text(
                                text = "${post.likes}",
                                style = TextStyle(
                                    fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(500), color = colorScheme.tertiary,
                                    textAlign = TextAlign.Start,
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.padding(end = 24.dp).width(62.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.comentario),
                                contentDescription = "Comentario",
                                tint = colorScheme.tertiary,
                                modifier = Modifier.size(18.dp).clickable { onComentarClick(post) }
                            )
                            Text(
                                text = "${post.comentarios}",
                                style = TextStyle(
                                    fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(500), color = colorScheme.tertiary,
                                    textAlign = TextAlign.Start,
                                )
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(R.drawable.cerrarsesion),
                        contentDescription = "Guardar",
                        tint = if (guardadoExitoso) colorScheme.primary else colorScheme.tertiary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(270f)
                            .clickable {
                                scope.launch {
                                    val bitmap = bitmapController.toImageBitmap().asAndroidBitmap()
                                    val exito = guardarPostComoImagen(contexto, bitmap)
                                    guardadoExitoso = exito
                                    Toast.makeText(
                                        contexto,
                                        if (exito) "✓ Guardado en galería" else "Error al guardar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 8.dp,
            color = if (colorScheme.outline == BordeOscuro) Color(0xFF303030) else colorScheme.outline
        )
    }
}
//
//Guardar Post
//
fun guardarPostComoImagen(contexto: Context, bitmap: Bitmap): Boolean {
    return try {
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "koruv_post_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Koruv")
        }
        val uri = contexto.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
        uri?.let {
            contexto.contentResolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
        true
    } catch (_: Exception) { false }
}
//
//ChatObjeto
//
@Composable
fun ChatObjeto(
    nombre: String,
    texto: String,
    sinLeer: Int,
    hora: String,
    foto: String = "",
    ultimoMensajeMio: Boolean = false,
    visto: Boolean = false,
    onChatButton: () -> Unit = {}
) {
    HorizontalDivider(color = colorScheme.outline)
    Row(
        Modifier
            .fillMaxWidth()
            .background(color = colorScheme.background)
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
            .clickable(onClick = onChatButton),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
        ) {
        if (foto.isNotEmpty()) {
            AsyncImage(
                model = foto,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.poordrawnplaceholder),
                placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.poordrawnplaceholder),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }
        Column(
            Modifier
                .padding(start = 16.dp)
                .height(40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nombre,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = colorScheme.onBackground,
                    )
                )
                Text(
                    text = hora,
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(500),
                        color = colorScheme.primary,
                    )
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (texto != "") {
                    Text(
                        text = if (texto.length > 20) texto.take(20) + "..." else texto,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(500),
                            color = if (sinLeer == 0) colorScheme.tertiary else colorScheme.onBackground,
                        )
                    )
                }
                else
                    Text(
                        text = "¡Empieza una conversación!",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(500),
                            color = colorScheme.tertiary,
                            )
                    )

                if (sinLeer != 0) {
                    Row(
                        Modifier
                            .width(20.dp)
                            .height(20.dp)
                            .background(
                                color = colorScheme.secondary,
                                shape = RoundedCornerShape(size = 33554400.dp)
                            )
                            .padding(start = 6.84.dp, top = 2.dp, end = 6.85.dp, bottom = 3.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sinLeer.toString(),
                            style = TextStyle(
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(700),
                                color = BlancoPuro,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                } else if (ultimoMensajeMio) {
                    Icon(
                        painter = painterResource(if (visto) R.drawable.read else R.drawable.unread),
                        contentDescription = if (visto) "Visto" else "Enviado",
                        tint = if (visto) colorScheme.primary else colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
//
//AutorRow
//
@Composable
private fun AutorRow(
    post: Post,
    onPerfilClick: (String) -> Unit,
    sobreImagen: Boolean = false
) {
    val colorTexto = if (sobreImagen) BlancoPuro else colorScheme.onBackground
    val colorSub = if (sobreImagen) BlancoPuro else colorScheme.tertiary

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onPerfilClick(post.autorId) }
    ) {
        if (post.autorFoto.isNotEmpty()) {
            AsyncImage(
                model = post.autorFoto,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.poordrawnplaceholder),
                placeholder = painterResource(R.drawable.poordrawnplaceholder),
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.poordrawnplaceholder),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        }
        Column {
            Text(
                text = post.autorNombre,
                maxLines = 1,
                modifier = Modifier
                    .widthIn(max = 160.dp)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        initialDelayMillis = 2000,
                        repeatDelayMillis = 2000,
                        spacing = MarqueeSpacing(20.dp)
                    ),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(600),
                    color = colorTexto,
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${post.autorUsuario} • ",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorSub,
                    )
                )
                Text(
                    text = fechaFormateada(post.timestamp),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorSub,
                    )
                )
            }
        }
    }
}
//
//OpcionesPost
//
@Composable
private fun MenuOpciones(
    post: Post,
    esMiPost: Boolean,
    yaSigue: Boolean,
    menuExpandido: Boolean,
    onMenuExpandido: (Boolean) -> Unit,
    onItemClick: (String) -> Unit,
    onEliminar: (String) -> Unit,
    onSeguirClick: (String) -> Unit,
    sobreImagen: Boolean = false
) {
    if (esMiPost) {
        Box {
            IconButton(onClick = { onMenuExpandido(true) }) {
                Icon(
                    painter = painterResource(R.drawable.opciones),
                    contentDescription = "Opciones",
                    tint = if (sobreImagen) BlancoPuro else colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { onMenuExpandido(false) },
                modifier = Modifier.background(colorScheme.background)
            ) {
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = {
                        onMenuExpandido(false)
                        onItemClick("editar/${post.id}?texto=${Uri.encode(post.texto)}&imagen=${Uri.encode(post.imagenUrl ?: "")}")
                    },
                    modifier = Modifier.background(colorScheme.background)
                )
                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    onClick = {
                        onMenuExpandido(false)
                        onEliminar(post.id)
                    },
                    modifier = Modifier.background(colorScheme.background)
                )
            }
        }
    } else {
        if (sobreImagen) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (yaSigue) Color(0x39CBCBCB) else Color(0x6040A3FF), RoundedCornerShape(10.dp))
                    .border(1.dp, BlancoPuro.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .clickable { onSeguirClick(post.autorId) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    if (yaSigue) "Siguiendo" else "Seguir",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = BlancoPuro,
                    )
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (yaSigue) Color.LightGray else colorScheme.primary)
                    .clickable { onSeguirClick(post.autorId) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (yaSigue) "Siguiendo" else "Seguir",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = colorScheme.background,
                    )
                )
            }
        }
    }
}
//
//BuscarObjetos
//
@Composable
fun BuscarObjetos(
    texto: String,
    iconoimagen: Painter? = null,
    subtexto: String = "",
    coloricono: Color = colorScheme.onBackground,
    colorobjeto: Color = Color(0x80E8E8E8),
    notif: Int = 0,
    onObjetoClick: () -> Unit = {}
) {
    HorizontalDivider(color = colorScheme.outline)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorScheme.background)
            .clickable { onObjetoClick() }
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = colorobjeto, shape = RoundedCornerShape(size = 24.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconoimagen != null) {
                    Icon(
                        painter = iconoimagen,
                        contentDescription = texto,
                        tint = coloricono
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = texto,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = colorScheme.onBackground,
                    )
                )
                Text(
                    text = subtexto,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = colorScheme.tertiary,
                    )
                )
            }
        }
        Icon(painter = painterResource(R.drawable.flecha), contentDescription = "Flecha", tint = colorScheme.tertiary)
    }
    HorizontalDivider(color = colorScheme.outline)
}
//
//MensajesBurbuja
//
@Composable
fun MensajesBurbuja(
    propio: Boolean,
    texto: String = "",
    hora: String = "",
    leido: Boolean = true,
    recibido: Boolean = false,
    fotoAutor: String = "",
    mostrarFoto: Boolean = false
) {
    val darkTheme: Boolean = isSystemInDarkTheme()
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (propio)
            Alignment.CenterEnd
        else
            Alignment.CenterStart
    ) {
        if (!propio) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {
                if (mostrarFoto) {
                    if (fotoAutor.isNotEmpty()) {
                        AsyncImage(
                            model = fotoAutor,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.poordrawnplaceholder),
                            placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.poordrawnplaceholder),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(35.dp))
                }
                Column(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .wrapContentWidth()
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x1A000000)
                        )
                        .shadow(
                            elevation = 6.dp,
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x1A000000)
                        )
                        .border(
                            width = 1.25.dp,
                            color = Color(0x99E2E8F0),
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                topEnd = 16.dp,
                                topStart = 8.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .background(
                            color = colorScheme.background,
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                topEnd = 16.dp,
                                topStart = 8.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = texto,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
                            color = colorScheme.onBackground,
                        )
                    )
                    Text(
                        text = hora,
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
                            color = if (darkTheme) Color(0xFFBCBCC0) else Color(0xFFA4A2A2),
                        )
                    )
                }
            }
        }
        else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .wrapContentWidth()
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x1A000000)
                        )
                        .shadow(
                            elevation = 6.dp,
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x1A000000)
                        )
                        .border(
                            width = 1.25.dp,
                            color = Color(0x99E2E8F0),
                            shape = RoundedCornerShape(
                                bottomEnd = 16.dp,
                                topStart = 16.dp,
                                topEnd = 8.dp,
                                bottomStart = 16.dp
                            )
                        )
                        .background(
                            AzulKoruv,
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                topStart = 16.dp,
                                topEnd = 8.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = texto,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = BlancoPuro,
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = hora,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(400),
                                    color = if (darkTheme) Color(0xFFD2D2DA) else Color(0xFFBEBEBE),
                                )
                            )
                            if (recibido) {
                                Icon(
                                    painter = painterResource(R.drawable.read),
                                    contentDescription = "Check",
                                    tint = if (leido) colorScheme.primary else colorScheme.onSurfaceVariant
                                )
                            }
                            else {
                                Icon(
                                    painter = painterResource(R.drawable.unread),
                                    contentDescription = "Check",
                                    tint = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
//
//CampoMensaje
//
@Composable
fun CampoMensaje(
    valor: String,
    onValorChange: (String) -> Unit,
    onEnviar: () -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier
) {
    val altoPantalla = Dimensiones.altoPantalla()
    var textFieldValue by remember { mutableStateOf(TextFieldValue(valor)) }
    LaunchedEffect(valor) {
        if (valor != textFieldValue.text) {
            textFieldValue = TextFieldValue(valor)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(top = 6.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = textFieldValue,
            onValueChange = { nuevo ->
                textFieldValue = nuevo
                onValorChange(nuevo.text)
            },
            modifier = Modifier
                .weight(1f)
                .then(textFieldModifier)
                .border(1.dp, colorScheme.outline, RoundedCornerShape(altoPantalla * 0.01308F))
                .background(Color(0x4DE8E8E8), RoundedCornerShape(altoPantalla * 0.01308F)),
            textStyle = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorScheme.onBackground,
            ),
            placeholder = {
                Text(
                    text = "Mensaje...",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    )
                )
            },
            maxLines = 5,
            minLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(altoPantalla * 0.01308F)
        )

        Box(
            modifier = Modifier
                .size(altoPantalla * 0.05452F)
                .clip(CircleShape)
                .background(
                    if (valor.isNotBlank()) colorScheme.primary else colorScheme.outline
                )
                .clickable(enabled = valor.isNotBlank()) { onEnviar()},
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.enviar),
                contentDescription = "Enviar",
                tint = BlancoPuro,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
//
//PerfilFormato
//
fun LazyListScope.perfilesFormato(
    usuarios: List<Map<String, String>>,
    onPerfilClick: (Map<String, String>) -> Unit = {}
) {
    items(usuarios) { usuario ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.background)
                .clickable { onPerfilClick(usuario) }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = usuario["foto"],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.poordrawnplaceholder),
                placeholder = painterResource(R.drawable.poordrawnplaceholder),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(
                    text = usuario["nombre"] ?: "",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = colorScheme.onBackground
                    )
                )
                Text(
                    text = "@${usuario["usuario"]}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        color = colorScheme.tertiary
                    )
                )
            }
        }
    }
}
//
//ComentariosObjeto
//
@Composable
fun ComentariosFormato(
    comentarios: List<Comentario>,
    onLike: (String) -> Unit,
    onEliminarComentario: (String) -> Unit = {},
    onPerfilClick: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        comentarios.forEach { comentario ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val esMiComentario = comentario.autorId == userId
            val yaLikeo = userId in comentario.likedBy
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (comentario.autorFoto.isNotEmpty()) {
                    AsyncImage(
                        model = comentario.autorFoto,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.poordrawnplaceholder),
                        placeholder = painterResource(id = R.drawable.poordrawnplaceholder),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onPerfilClick(comentario.autorId) }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.poordrawnplaceholder),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onPerfilClick(comentario.autorId) }
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comentario.autorNombre,
                            modifier = Modifier
                                .widthIn(max = 140.dp)
                                .basicMarquee(
                                    iterations = Int.MAX_VALUE,
                                    initialDelayMillis = 2000,
                                    repeatDelayMillis = 2000,
                                    spacing = MarqueeSpacing(20.dp)
                                )
                                .clickable { onPerfilClick(comentario.autorId) },
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                        Text(
                            text = "@${comentario.autorUsuario} • ${fechaFormateada(comentario.timestamp)}",
                            modifier = Modifier.clickable { onPerfilClick(comentario.autorId) },
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = colorScheme.tertiary,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comentario.texto,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily(Font(R.font.inter_regular))
                            )
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    painter = if (yaLikeo) painterResource(R.drawable.corazonlike) else painterResource(R.drawable.corazon),
                                    contentDescription = "Me gusta",
                                    tint = if (yaLikeo) colorScheme.secondary else colorScheme.tertiary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable(onClick = { onLike(comentario.id) })
                                )
                                Text(
                                    text = comentario.likes.toString(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(500),
                                        color = colorScheme.tertiary,
                                        textAlign = TextAlign.Start,
                                    )
                                )
                            }
                        }
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (esMiComentario) {
                                    var menuComentario by remember { mutableStateOf(false) }
                                    Box {
                                        Icon(
                                            painter = painterResource(R.drawable.opciones),
                                            contentDescription = "Opciones",
                                            tint = colorScheme.tertiary,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { menuComentario = true }
                                        )
                                        DropdownMenu(
                                            expanded = menuComentario,
                                            onDismissRequest = { menuComentario = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Eliminar") },
                                                onClick = {
                                                    menuComentario = false
                                                    onEliminarComentario(comentario.id)
                                                },
                                                modifier = Modifier.background(colorScheme.background)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = colorScheme.outline)
        }
    }
}
//
//Fecha
//
fun fechaFormateada(timestamp: Long): String {
    val ahora = System.currentTimeMillis()
    val diff = ahora - timestamp

    val minutos = diff / (1000 * 60)
    val horas = diff / (1000 * 60 * 60)
    val dias = diff / (1000 * 60 * 60 * 24)
    val semanas = dias / 7
    val meses = dias / 30
    val anos = dias / 365

    return when {
        minutos < 1 -> "Ahora mismo"
        minutos < 60 -> "Hace $minutos min"
        horas < 24 -> "Hace $horas h"
        dias < 7 -> "Hace $dias días"
        semanas < 4 -> "Hace $semanas semanas"
        meses < 12 -> "Hace $meses meses"
        else -> "Hace $anos años"
    }
}