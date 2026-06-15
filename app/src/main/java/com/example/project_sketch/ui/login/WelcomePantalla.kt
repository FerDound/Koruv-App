@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.project_sketch.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_sketch.R

@Composable
fun WelcomePantalla(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    val densidadPantalla = Dimensiones.densidadPantalla()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = anchoPantalla * 0.05825F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(altoPantalla * 0.11995F))
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
                color = colorScheme.onSurface,
                letterSpacing = with(densidadPantalla) { (anchoPantalla * 0.0171f).toSp() },
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        //Texto
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

        Spacer(modifier = Modifier.height(altoPantalla * 0.2181F))
        //Botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = altoPantalla * 0.03489F),
            verticalArrangement = Arrangement.spacedBy(altoPantalla * 0.01744F),
        ) {
            BotonAnimado(
                text = "Iniciar sesión",
                textColor = colorScheme.background,
                backgroundColor = colorScheme.onSurface,
                onClick = onLoginClick
            )
            Spacer(modifier = Modifier.height(altoPantalla * 0.01744F))
            BotonAnimado(
                text = "Crear cuenta",
                textColor = colorScheme.onSurface,
                borderColor = colorScheme.outline,
                onClick = onSignupClick
            )
        }
        Spacer(modifier = Modifier.weight(0.05f))
    }
}