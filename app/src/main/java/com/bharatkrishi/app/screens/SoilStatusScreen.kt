package com.bharatkrishi.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.utils.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilStatusScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        LocalizationManager.get("Soil Health"),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        SoilAnalysisContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun SoilAnalysisContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Soil Test Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Science,
                            contentDescription = "Soil Test",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                LocalizationManager.get("Latest Soil Test Results"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                LocalizationManager.get("Updated 5 days ago"),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            // Soil Parameters
            val soilParams = listOf(
                SoilParameter(
                    LocalizationManager.get("pH Level"),
                    "6.5",
                    LocalizationManager.get("Slightly Acidic"),
                    Color(0xFF4CAF50)
                ),
                SoilParameter(
                    LocalizationManager.get("Nitrogen (N)"),
                    "180 kg/ha",
                    LocalizationManager.get("Medium"),
                    Color(0xFFFF9800)
                ),
                SoilParameter(
                    LocalizationManager.get("Phosphorus (P)"),
                    "25 ppm",
                    LocalizationManager.get("High"),
                    Color(0xFF4CAF50)
                ),
                SoilParameter(
                    LocalizationManager.get("Potassium (K)"),
                    "120 ppm",
                    LocalizationManager.get("Low"),
                    Color(0xFFf44336)
                ),
                SoilParameter(
                    LocalizationManager.get("Organic Matter"),
                    "2.8%",
                    LocalizationManager.get("Good"),
                    Color(0xFF4CAF50)
                ),
                SoilParameter(
                    LocalizationManager.get("Soil Moisture"),
                    "65%",
                    LocalizationManager.get("Optimal"),
                    Color(0xFF2196F3)
                )
            )

            Text(
                LocalizationManager.get("Soil Parameters"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            soilParams.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { param ->
                        SoilParameterCard(
                            param,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            // Recommendations
            Text(
                LocalizationManager.get("Recommendations"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val recommendations = listOf(
                LocalizationManager.get("Add potassium-rich fertilizer to improve K levels"),
                LocalizationManager.get("Consider lime application to increase pH slightly"),
                LocalizationManager.get("Maintain current nitrogen management practices"),
                LocalizationManager.get("Good phosphorus levels - no action needed")
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    recommendations.forEach { recommendation ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Recommendation",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                recommendation,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item {
            // Action Button
            Button(
                onClick = { /* Schedule new soil test */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Schedule, contentDescription = "Schedule")
                Spacer(modifier = Modifier.width(8.dp))
                Text(LocalizationManager.get("Schedule New Soil Test"))
            }
        }
    }
}

// Data Classes & Components needed
data class SoilParameter(
    val name: String,
    val value: String,
    val status: String,
    val color: Color
)

@Composable
fun SoilParameterCard(param: SoilParameter, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(param.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                param.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(param.color, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    param.status,
                    fontSize = 12.sp,
                    color = param.color,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
