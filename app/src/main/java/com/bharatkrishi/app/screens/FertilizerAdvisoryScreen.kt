package com.bharatkrishi.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilizerAdvisoryScreen(navController: NavController) {
    val fertilizerRecommendations = listOf(
        FertilizerRecommendation(
            cropName = "Wheat",
            stage = "Sowing (Basal)",
            fertilizer = "DAP + MOP",
            quantity = "55kg DAP + 40kg MOP / acre",
            timing = "At time of sowing",
            description = "Provides essential Phosphorus and Potassium. Potassium helps resist Rust diseases."
        ),
        FertilizerRecommendation(
            cropName = "Wheat",
            stage = "CRI Stage (20-25 DAS)",
            fertilizer = "Urea",
            quantity = "45-50 kg / acre",
            timing = "With first irrigation",
            description = "Crucial for tillering. Avoid excess Nitrogen to prevent Rust susceptibility."
        ),
        FertilizerRecommendation(
            cropName = "Wheat",
            stage = "Jointing Stage (40-45 DAS)",
            fertilizer = "Urea",
            quantity = "45-50 kg / acre",
            timing = "With second irrigation",
            description = "Supports stem elongation. Balanced nutrition reduces Mildew risk."
        ),
        FertilizerRecommendation(
            cropName = "Wheat",
            stage = "Booting Stage",
            fertilizer = "Zinc Sulphate (if deficiency)",
            quantity = "10 kg / acre (soil application)",
            timing = "If leaves show yellowing",
            description = "Corrects Zinc deficiency. Healthy plants resist Septoria better."
        ),
        FertilizerRecommendation(
            cropName = "Mustard",
            stage = "Sowing",
            fertilizer = "SSP + Urea",
            quantity = "150kg SSP + 30kg Urea / acre",
            timing = "Basal application",
            description = "Sulphur in SSP is vital for oil content and disease resistance."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = { Text("Fertilizer Advisory", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(fertilizerRecommendations) { recommendation ->
                FertilizerCard(recommendation)
            }
        }
    }
}

@Composable
fun FertilizerCard(recommendation: FertilizerRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    recommendation.cropName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = { },
                    label = { Text(recommendation.stage, fontSize = 12.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FertilizerDetailRow("Fertilizer", recommendation.fertilizer, Icons.Default.Science)
            FertilizerDetailRow("Quantity", recommendation.quantity, Icons.Default.Scale)
            FertilizerDetailRow("Timing", recommendation.timing, Icons.Default.Schedule)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                recommendation.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FertilizerDetailRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

data class FertilizerRecommendation(
    val cropName: String,
    val stage: String,
    val fertilizer: String,
    val quantity: String,
    val timing: String,
    val description: String
)
