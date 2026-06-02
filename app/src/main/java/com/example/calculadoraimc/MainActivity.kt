package com.example.calculadoraimc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppIMC()
        }
    }
}

@Composable
fun AppIMC() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "input") {
        composable("input") { InputScreen(navController) }
        composable(
            route = "resultado/{nombre}/{imc}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("imc") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val imc = backStackEntry.arguments?.getFloat("imc") ?: 0f
            ResultScreen(navController, nombre, imc)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calculadora de IMC") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text("Altura (m)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (error) {
                Text(
                    text = "Por favor, ingresa valores válidos",
                    color = Color.Red
                )
            }

            Button(
                onClick = {
                    val pesoVal = peso.toFloatOrNull()
                    val alturaVal = altura.toFloatOrNull()
                    if (pesoVal != null && alturaVal != null && pesoVal > 0 && alturaVal > 0) {
                        val imc = pesoVal / (alturaVal * alturaVal)
                        navController.navigate("resultado/$nombre/$imc")
                        error = false
                        // limpiar campos al volver
                        nombre = ""
                        peso = ""
                        altura = ""
                    } else {
                        error = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, nombre: String, imc: Float) {
    val categoria: String
    val color: Color

    when {
        imc < 18.5 -> {
            categoria = "Bajo peso"
            color = Color(0xFF1B3CE2) // Azul
        }
        imc in 18.5..24.9 -> {
            categoria = "Peso normal"
            color = Color.Green // Verde
        }
        imc in 25.0..29.9 -> {
            categoria = "Sobrepeso"
            color = Color(0xFFFF9800) // Anaranjado
        }
        else -> {
            categoria = "Obesidad"
            color = Color.Red
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resultado IMC") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hola $nombre, tu resultado es:")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format(
                java.util.Locale.US,
                "IMC: %.1f",
                imc
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = categoria, color = color)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
                }
        }
    }
}