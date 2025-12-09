package com.bharatkrishi.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.utils.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropAdvisoryScreen(navController: NavController) {
    val cropRecommendations = listOf(
        CropRecommendation(
            name = LocalizationManager.get("Wheat"),
            suitability = LocalizationManager.get("Excellent for winter season"),
            marketInfo = LocalizationManager.get("High demand, good price"),
            color = Color(0xFF4CAF50),
            season = LocalizationManager.get("Rabi Season")
        ),
        CropRecommendation(
            name = LocalizationManager.get("Cotton"),
            suitability = LocalizationManager.get("Suitable for your soil type"),
            marketInfo = LocalizationManager.get("Premium variety available"),
            color = Color(0xFF2196F3),
            season = LocalizationManager.get("Kharif Season")
        ),
        CropRecommendation(
            name = LocalizationManager.get("Sugarcane"),
            suitability = LocalizationManager.get("Good irrigation available"),
            marketInfo = LocalizationManager.get("Processing unit nearby"),
            color = Color(0xFFFF9800),
            season = LocalizationManager.get("Year Round")
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Crop Advisory"), 
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) 
                    // Keeping primary here as it works as a header block, or could use SurfaceVariant like Profile. 
                    // Let's use Primary (Orange) to mimic Home's "Welcome" card style for consistency.
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            LocalizationManager.get("Crop Recommendations"),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            LocalizationManager.get("Based on your farm history, soil type, and market trends"),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            items(cropRecommendations) { crop ->
                CropRecommendationCard(crop)
            }
        }
    }
}

@Composable
fun CropRecommendationCard(crop: CropRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // White/DarkSurface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(crop.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Agriculture,
                    contentDescription = crop.name,
                    tint = crop.color,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        crop.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(crop.season, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = crop.color.copy(alpha = 0.2f)
                        )
                    )
                }

                Text(
                    crop.suitability,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    crop.marketInfo,
                    fontSize = 12.sp,
                    color = crop.color, // Keep semantic color for emphasis
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class CropRecommendation(
    val name: String,
    val suitability: String,
    val marketInfo: String,
    val color: Color,
    val season: String
)

