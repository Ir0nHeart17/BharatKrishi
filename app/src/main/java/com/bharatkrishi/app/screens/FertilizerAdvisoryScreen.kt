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
import com.bharatkrishi.app.utils.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilizerAdvisoryScreen(navController: NavController) {
    val fertilizerRecommendations = listOf(
        FertilizerRecommendation(
            cropName = LocalizationManager.get("Wheat"),
            stage = LocalizationManager.get("Sowing (Basal)"),
            fertilizer = LocalizationManager.get("DAP + MOP"),
            quantity = "55kg DAP + 40kg MOP / acre",
            timing = LocalizationManager.get("At time of sowing"),
            description = LocalizationManager.get("Provides essential Phosphorus and Potassium. Potassium helps resist Rust diseases.")
        ),
        FertilizerRecommendation(
            cropName = LocalizationManager.get("Wheat"),
            stage = LocalizationManager.get("CRI Stage (20-25 DAS)"),
            fertilizer = LocalizationManager.get("Urea"),
            quantity = "45-50 kg / acre",
            timing = LocalizationManager.get("With first irrigation"),
            description = LocalizationManager.get("Crucial for tillering. Avoid excess Nitrogen to prevent Rust susceptibility.")
        ),
        FertilizerRecommendation(
            cropName = LocalizationManager.get("Wheat"),
            stage = LocalizationManager.get("Jointing Stage (40-45 DAS)"),
            fertilizer = LocalizationManager.get("Urea"),
            quantity = "45-50 kg / acre",
            timing = LocalizationManager.get("With second irrigation"),
            description = LocalizationManager.get("Supports stem elongation. Balanced nutrition reduces Mildew risk.")
        ),
        FertilizerRecommendation(
            cropName = LocalizationManager.get("Wheat"),
            stage = LocalizationManager.get("Booting Stage"),
            fertilizer = LocalizationManager.get("Zinc Sulphate (if deficiency)"),
            quantity = "10 kg / acre (soil application)",
            timing = LocalizationManager.get("If leaves show yellowing"),
            description = LocalizationManager.get("Corrects Zinc deficiency. Healthy plants resist Septoria better.")
        ),
        FertilizerRecommendation(
            cropName = LocalizationManager.get("Mustard"),
            stage = LocalizationManager.get("Sowing"),
            fertilizer = LocalizationManager.get("SSP + Urea"),
            quantity = "150kg SSP + 30kg Urea / acre",
            timing = LocalizationManager.get("Basal application"),
            description = LocalizationManager.get("Sulphur in SSP is vital for oil content and disease resistance.")
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Fertilizer Advisory"), 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                AssistChip(
                    onClick = { },
                    label = { Text(recommendation.stage, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FertilizerDetailRow(LocalizationManager.get("Fertilizer"), recommendation.fertilizer, Icons.Default.Science)
            FertilizerDetailRow(LocalizationManager.get("Quantity"), recommendation.quantity, Icons.Default.Scale)
            FertilizerDetailRow(LocalizationManager.get("Timing"), recommendation.timing, Icons.Default.Schedule)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                recommendation.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
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
