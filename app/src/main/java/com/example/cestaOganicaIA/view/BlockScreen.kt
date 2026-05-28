package com.example.cestaOganicaIA.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme

@Composable
fun BlockScreen(navController: NavController) {
    val colorVerde = Color(0xFF4CAF50)

    HuertoHogarTheme {
        HuertoScaffold(
            titulo = "Bloque Informativo",
            navController = navController
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🌿 Productos Orgánicos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorVerde
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Los productos orgánicos son cultivados sin pesticidas ni químicos artificiales, lo que ayuda a cuidar tanto tu salud como el medio ambiente. " +
                                "Consumir alimentos naturales fortalece el sistema inmunológico y aporta nutrientes más puros, respetando los ciclos naturales de la tierra.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        text = "🌱 Además, elegir productos orgánicos fomenta una agricultura más sostenible, apoya a pequeños productores locales y reduce la contaminación del suelo y del agua.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Justify
                    )

                    HorizontalDivider(
                        color = colorVerde.copy(alpha = 0.6f),
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Text(
                        text = "Cuidar de tu cuerpo también es cuidar del planeta 🌎",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colorVerde,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
