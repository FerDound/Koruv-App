package com.example.project_sketch.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Person
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
fun PersonalizacionPantalla(
    onBackClick: () -> Unit,
    onContinuarClick: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    nombre: String = "",
    apellidoPaterno: String = "",
    apellidoMaterno: String = "",
    numeroControl: String = "",
    nombrepublico: String = "",
    carrera: String = "",
    semestre: String = "",
    onNombreChange: (String) -> Unit = {},
    onApellidoPaternoChange: (String) -> Unit = {},
    onApellidoMaternoChange: (String) -> Unit = {},
    onNumeroControlChange: (String) -> Unit = {},
    onNombrepublicoChange: (String) -> Unit = {},
    onCarreraChange: (String) -> Unit = {},
    onSemestreChange: (String) -> Unit = {}
) {
    val altoPantalla = Dimensiones.altoPantalla()
    val anchoPantalla = Dimensiones.anchoPantalla()
    val densidadPantalla = Dimensiones.densidadPantalla()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = anchoPantalla * 0.05825F)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars),
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
                modifier = Modifier.size(anchoPantalla * 0.15291F, altoPantalla * 0.0567F),
                painter = painterResource(id = R.drawable.logo_koruv),
                contentDescription = "Logo Koruv",
                contentScale = ContentScale.Fit
            )
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

        Text(
            text = "Cuéntanos un poco más sobre ti",
            style = TextStyle(
                fontSize = (anchoPantalla * 0.03398F).value.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(altoPantalla * 0.06106F))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(altoPantalla * 0.01744F)
        ) {
            CampoDeDatos("Nombre público", nombrepublico, onNombrepublicoChange, Icons.Outlined.Person)
            CampoDeDatos("Nombre(s)", nombre, onNombreChange, Icons.Outlined.Person)
            CampoDeDatos("Apellido paterno", apellidoPaterno, onApellidoPaternoChange, Icons.Outlined.Person)
            CampoDeDatos("Apellido materno (opcional)", apellidoMaterno, onApellidoMaternoChange, Icons.Outlined.Person)
            CampoDeDatos("Número de control", numeroControl, onNumeroControlChange, Icons.Outlined.Badge)

            CampoDropdown(
                text = "Especialidad",
                valor = carrera,
                opciones = listOf(
                    "Alimentos", "Ciberseguridad", "Contabilidad", "Electrónica", "Laboratorista", "Mantenimiento", "Programación"
                ),
                onSeleccion = onCarreraChange,
                iconPainter = painterResource(R.drawable.tag)
            )

            CampoDropdown(
                text = "Grupo",
                valor = semestre,
                opciones = listOf(1, 2, 3, 4, 5, 6).flatMap { num ->
                    listOf("A", "B").flatMap { letra ->
                        listOf("M", "V").map { turno -> "${num}${letra}${turno}" }
                    }
                },
                onSeleccion = onSemestreChange,
                iconPainter = painterResource(R.drawable.tag)
            )
        }
        Spacer(modifier = Modifier.height(altoPantalla * 0.06106F))
        Column(horizontalAlignment = Alignment.Start) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = TextStyle(
                        fontSize = (anchoPantalla * 0.03398F).value.sp,
                        color = Color.Red,
                        fontFamily = FontFamily(Font(R.font.inter_regular))
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = altoPantalla * 0.01744F)
                )
            }
            BotonAnimado(
                text = if (isLoading) "Creando cuenta..." else "Continuar",
                textColor = colorScheme.surface,
                backgroundColor = if (isLoading) Color.Gray else colorScheme.onBackground,
                enabled = !isLoading,
                onClick = onContinuarClick
            )
        }
        Spacer(modifier = Modifier.height(altoPantalla * 0.03F))
    }
}