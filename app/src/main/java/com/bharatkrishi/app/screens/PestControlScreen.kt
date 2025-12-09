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
import com.bharatkrishi.app.utils.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestControlScreen(navController: NavController) {
    val pests = listOf(
        PestInfo(
            name = LocalizationManager.get("Yellow Rust (Stripe Rust)"),
            description = LocalizationManager.get("Yellow stripes on leaves. Affects photosynthesis. Use Propiconazole."),
            severity = LocalizationManager.get("High"),
            severityColor = Color(0xFFf44336)
        ),
        PestInfo(
            name = LocalizationManager.get("Brown Rust (Leaf Rust)"),
            description = LocalizationManager.get("Orange-brown pustules on leaves. Spread by wind. Use Tebuconazole."),
            severity = LocalizationManager.get("High"),
            severityColor = Color(0xFFf44336)
        ),
        PestInfo(
            name = LocalizationManager.get("Loose Smut"),
            description = LocalizationManager.get("Black powdery mass replacing grain heads. Seed treatment with Carboxin is effective."),
            severity = LocalizationManager.get("Medium"),
            severityColor = Color(0xFFFF9800)
        ),
        PestInfo(
            name = LocalizationManager.get("Septoria"),
            description = LocalizationManager.get("Yellow spots on leaves with black dots. Causes leaf blotch. Use fungicides."),
            severity = LocalizationManager.get("Medium"),
            severityColor = Color(0xFFFF9800)
        ),
        PestInfo(
            name = LocalizationManager.get("Powdery Mildew"),
            description = LocalizationManager.get("White powdery growth on leaves. Reduces yield. Use Sulphur-based fungicides."),
            severity = LocalizationManager.get("Medium"),
            severityColor = Color(0xFFFF9800)
        ),
        PestInfo(
            name = LocalizationManager.get("Stripe Rust"),
            description = LocalizationManager.get("Similar to Yellow Rust. Causes significant yield loss in cool, moist conditions."),
            severity = LocalizationManager.get("High"),
            severityColor = Color(0xFFf44336)
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Wheat Disease Guide"), 
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    pest.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "${pest.severity} ${LocalizationManager.get("Risk")}",
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
