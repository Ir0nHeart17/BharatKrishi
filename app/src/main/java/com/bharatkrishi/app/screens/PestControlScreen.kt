package com.bharatkrishi.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestControlScreen(navController: NavController) {
    val pests = listOf(
        PestInfo(
            name = "Yellow Rust (Stripe Rust)",
            description = "Yellow stripes on leaves. Affects photosynthesis. Use Propiconazole.",
            severity = "High",
            severityColor = Color(0xFFf44336)
        ),
        PestInfo(
            name = "Brown Rust (Leaf Rust)",
            description = "Orange-brown pustules on leaves. Spread by wind. Use Tebuconazole.",
            severity = "High",
            severityColor = Color(0xFFf44336)
        ),
        PestInfo(
            name = "Loose Smut",
            description = "Black powdery mass replacing grain heads. Seed treatment with Carboxin is effective.",
            severity = "Medium",
            severityColor = Color(0xFFFF9800)
        ),
        PestInfo(
            name = "Septoria",
            description = "Yellow spots on leaves with black dots. Causes leaf blotch. Use fungicides.",
            severity = "Medium",
            severityColor = Color(0xFFFF9800)
        ),
        PestInfo(
            name = "Powdery Mildew",
            description = "White powdery growth on leaves. Reduces yield. Use Sulphur-based fungicides.",
            severity = "Medium",
            severityColor = Color(0xFFFF9800)
        ),
        PestInfo(
            name = "Stripe Rust",
            description = "Similar to Yellow Rust. Causes significant yield loss in cool, moist conditions.",
            severity = "High",
            severityColor = Color(0xFFf44336)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = { Text("Wheat Disease Guide", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pests) { pest ->
                PestCard(pest)
            }
        }
    }
}

@Composable
fun PestCard(pest: PestInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(pest.severityColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    pest.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    pest.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                "${pest.severity} Risk",
                fontSize = 12.sp,
                color = pest.severityColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class PestInfo(
    val name: String,
    val description: String,
    val severity: String,
    val severityColor: Color
)
