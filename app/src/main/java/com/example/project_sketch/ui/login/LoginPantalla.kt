package com.example.project_sketch.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun LoginPantalla(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    val densidadPantalla = Dimensiones.densidadPantalla()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(colorScheme.background)
            .padding(horizontal = (anchoPantalla * 0.05825F)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            Image(
                modifier = Modifier.size(anchoPantalla*0.15291F, altoPantalla*0.0567F),
                painter = painterResource(id = R.drawable.logo_koruv),
                contentDescription = "Logo Koruv",
                contentScale = ContentScale.Fit
            )
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
                    color = colorScheme.onSurface,
                    letterSpacing = with(densidadPantalla) { (anchoPantalla * 0.0171f).toSp() },
                )
            )
        }
        Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
        Text(
            text = "Ingresa tus datos para continuar",
            modifier = Modifier.wrapContentWidth(),
            style = TextStyle(
                fontSize = (anchoPantalla*0.03398F).value.sp,
                lineHeight = (altoPantalla*0.02181F).value.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(altoPantalla * 0.11559F))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            CampoDeDatos("Email", email, onEmailChange, Icons.Outlined.Email)
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            CampoDeDatos("Contraseña", password, onPasswordChange, Icons.Outlined.Lock, true)
            Spacer(modifier = Modifier.height(altoPantalla * 0.01308F))

        }
        Spacer(modifier = Modifier.height(altoPantalla * 0.15F))
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
            BotonAnimado(
                text = if (isLoading) "Iniciando sesión..." else "Iniciar sesión",
                textColor = colorScheme.background,
                backgroundColor = if (isLoading) Color.Gray else colorScheme.onBackground,
                enabled = !isLoading,
                onClick = onLoginClick
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
                        .padding(horizontal = anchoPantalla*0.01941F),
                    style = TextStyle(
                        fontSize = (anchoPantalla*0.03398F).value.sp,
                        color = colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    )
                )
            }
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            BotonAnimado(
                text = "Crear cuenta",
                textColor = colorScheme.onBackground,
                borderColor = colorScheme.outline,
                onClick = onSignupClick
            )
        }
    }
}
