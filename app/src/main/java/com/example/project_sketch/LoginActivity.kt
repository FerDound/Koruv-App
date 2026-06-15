package com.example.project_sketch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_sketch.ui.login.WelcomePantalla
import com.example.project_sketch.ui.login.LoginPantalla
import com.example.project_sketch.ui.login.PersonalizacionPantalla
import com.example.project_sketch.ui.login.SingUpPantalla
import com.example.project_sketch.ui.theme.Project_sketchTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        val isLightTheme = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_NO
        windowInsetsController.isAppearanceLightStatusBars = isLightTheme
        windowInsetsController.isAppearanceLightNavigationBars = isLightTheme


        setContent {
            val temaViewModel: TemaViewModel = viewModel()
            val tema by temaViewModel.tema.collectAsState()

            val isDark = when (tema) {
                "oscuro" -> true
                "claro" -> false
                else -> isSystemInDarkTheme()
            }

            Project_sketchTheme(darkTheme = isDark) {}

            Project_sketchTheme {

                val navController = rememberNavController()
                val viewModel: AuthViewModel = viewModel()
                val currentUser = FirebaseAuth.getInstance().currentUser
                val startDestination = if (currentUser != null) "home" else "welcome"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("welcome") {
                        WelcomePantalla(
                            onLoginClick = { navController.navigate("login") },
                            onSignupClick = { navController.navigate("signup") }
                        )
                    }
                    composable("login") {
                        LoginPantalla(
                            onBackClick = { navController.navigateUp() },
                            onSignupClick = { navController.navigate("signup") },
                            onLoginClick = {
                                viewModel.login {
                                    navController.navigate("home")
                                }
                            },
                            isLoading = viewModel.isLoading,
                            errorMessage = viewModel.errorMessage,
                            email = viewModel.email,
                            password = viewModel.password,
                            onEmailChange = { viewModel.email = it },
                            onPasswordChange = { viewModel.password = it }
                        )
                    }
                    composable("signup") {
                        SingUpPantalla(
                            onBackClick = { navController.navigateUp() },
                            onLoginClick = { navController.navigate("login") },
                            onSignupClick = {
                                viewModel.validarSingUp {
                                    navController.navigate("personalizacion")
                                }
                            },
                            errorMessage = viewModel.errorMessage,
                            email = viewModel.email,
                            password = viewModel.password,
                            username = viewModel.username,
                            onEmailChange = { viewModel.email = it },
                            onPasswordChange = { viewModel.password = it },
                            onUsernameChange = { viewModel.username = it }
                        )
                    }
                    composable("personalizacion") {
                        PersonalizacionPantalla(
                            onBackClick = { navController.navigateUp() },
                            onContinuarClick = { viewModel.register { navController.navigate("home") } },
                            isLoading = viewModel.isLoading,
                            errorMessage = viewModel.errorMessage,
                            nombre = viewModel.nombre,
                            apellidoPaterno = viewModel.apellidoPaterno,
                            apellidoMaterno = viewModel.apellidoMaterno,
                            numeroControl = viewModel.numeroControl,
                            nombrepublico = viewModel.nombrepublico,
                            carrera = viewModel.carrera,
                            semestre = viewModel.semestre,
                            onNombreChange = { viewModel.nombre = it },
                            onApellidoPaternoChange = { viewModel.apellidoPaterno = it },
                            onApellidoMaternoChange = { viewModel.apellidoMaterno = it },
                            onNumeroControlChange = { viewModel.numeroControl = it },
                            onNombrepublicoChange = { viewModel.nombrepublico = it },
                            onCarreraChange = { viewModel.carrera = it },
                            onSemestreChange = { viewModel.semestre = it }
                        )
                    }
                    composable("home") {
                        val context = LocalContext.current

                        LaunchedEffect(Unit) {
                            try {
                                val uid = FirebaseAuth.getInstance().currentUser?.uid

                                if (uid == null) {
                                    context.startActivity(
                                        Intent(context, MainActivity::class.java)
                                    )
                                    (context as ComponentActivity).finish()
                                    return@LaunchedEffect
                                }

                                val db = FirebaseFirestore.getInstance()

                                val userDoc = db.collection("users")
                                    .document(uid)
                                    .get()
                                    .await()
                                val yaRespondio =
                                    userDoc.getBoolean("encuestaRespondida") ?: false

                                val semestre =
                                    userDoc.getString("semestre") ?: ""

                                val carrera =
                                    userDoc.getString("carrera") ?: ""

                                val configDoc = db.collection("config")
                                    .document("encuesta")
                                    .get()
                                    .await()

                                val encuestaActiva =
                                    configDoc.getBoolean("activa") ?: false

                                val excluirGrupo = semestre.equals("6AM", ignoreCase = true) && (carrera.equals("Programacion", ignoreCase = true) || carrera.equals("Programación", ignoreCase = true))

                                val mostrarEncuesta =
                                    encuestaActiva &&
                                            !yaRespondio &&
                                            !excluirGrupo

                                val destino =
                                    if (mostrarEncuesta)
                                        Intent(context, EncuestaActivity::class.java)
                                    else
                                        Intent(context, MainActivity::class.java)

                                context.startActivity(destino)
                                (context as ComponentActivity).finish()

                            } catch (_: Exception) {
                                context.startActivity(
                                    Intent(context, MainActivity::class.java)
                                )
                                (context as ComponentActivity).finish()
                            }
                        }
                    }
                }
            }
        }
    }
}