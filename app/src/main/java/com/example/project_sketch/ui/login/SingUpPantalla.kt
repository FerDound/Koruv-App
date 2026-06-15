package com.example.project_sketch.ui.login

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_sketch.R


@Composable
fun SingUpPantalla(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    errorMessage: String? = null,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    email: String,
    password: String,
    username: String,
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    val densidadPantalla = Dimensiones.densidadPantalla()
    var terminosAceptados by remember { mutableStateOf(false) }
    var mostrarTerminos by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    var confirmarPassword by remember { mutableStateOf("") }
    val mensajeAMostrar = errorMessage ?: errorLocal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = (anchoPantalla * 0.05825F))
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (mostrarTerminos) {
            TerminosModal(
                onAceptar = {
                    terminosAceptados = true
                    mostrarTerminos = false
                },
                onCerrar = {
                    mostrarTerminos = false
                }
            )
        }
        // Flecha atrás
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = altoPantalla * 0.0349F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = colorScheme.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(altoPantalla * 0.034896F))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            //Logo
            Image(
                modifier = Modifier.size(anchoPantalla*0.15291F, altoPantalla*0.0567F),
                painter = painterResource(id = R.drawable.logo_koruv),
                contentDescription = "Logo Koruv",
                contentScale = ContentScale.Fit
            )
            //Titulo
            Text(
                text = "KORUV",
                maxLines = 1,
                fontSize = TextUnit.Unspecified,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 24.sp,
                    maxFontSize = with(densidadPantalla) { (anchoPantalla * 0.15533f).toSp() },
                    stepSize = 1.sp
                ),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.josefin_sans_regular)),
                    color = colorScheme.onSurface,
                    letterSpacing = with(densidadPantalla) { (anchoPantalla * 0.0171f).toSp() },
                )
            )
        }

        Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))

        //Texto
        Text(
            text = "Ingresa tus datos para continuar",
            maxLines = 1,
            modifier = Modifier.wrapContentWidth(),
            style = TextStyle(
                fontSize = (anchoPantalla*0.03398F).value.sp,
                lineHeight = (altoPantalla*0.02181F).value.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(altoPantalla * 0.06106F))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {

            CampoDeDatos("@usuario", username, { if (it.length <= 15) onUsernameChange(it) }, Icons.Outlined.Person)
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            CampoDeDatos("Email", email, onEmailChange, Icons.Outlined.Email)
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            CampoDeDatos("Contraseña", password, onPasswordChange, Icons.Outlined.Lock, password = true)
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            CampoDeDatos("Confirmar contraseña", confirmarPassword, { confirmarPassword = it }, Icons.Outlined.Lock, password = true)
        }
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = TextStyle(
                        fontSize = (anchoPantalla * 0.03398F).value.sp,
                        color = Color.Red,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = altoPantalla * 0.01744F)
                )
            }
            // Casilla de términos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(altoPantalla * 0.06106F),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = terminosAceptados,
                    onCheckedChange = { checked ->
                        if (checked) mostrarTerminos = true
                        else terminosAceptados = false
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.outline
                    )
                )
                Text(
                    text = "He leído y acepto los Términos de Uso y la Política de Privacidad de Koruv.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        color = colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.clickable { mostrarTerminos = true }
                )
            }

            BotonAnimado(
                text = "Crear cuenta",
                textColor = if (terminosAceptados) colorScheme.surface else colorScheme.onSurfaceVariant,
                backgroundColor = if (terminosAceptados) colorScheme.onBackground else colorScheme.surface,
                enabled = terminosAceptados,
                onClick = {
                    if (password != confirmarPassword) {
                        errorLocal = "Las contraseñas no coinciden"
                    } else {
                        errorLocal = null
                        onSignupClick()
                    }
                }
            )
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = altoPantalla * 0.01744F, bottom = altoPantalla * 0.01744F),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(color = colorScheme.onSurfaceVariant)
                Text(
                    text = "O",
                    modifier = Modifier
                        .background(colorScheme.background)
                        .padding(horizontal =anchoPantalla*0.01941F),
                    style = TextStyle(
                        fontSize = (anchoPantalla*0.03398F).value.sp,
                        color = colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    )
                )
            }
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            BotonAnimado(
                text = "Iniciar sesión",
                textColor = colorScheme.onBackground,
                borderColor = colorScheme.outline,
                onClick = onLoginClick
            )
        }
    }
}
