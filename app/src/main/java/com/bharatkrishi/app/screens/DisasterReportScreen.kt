package com.bharatkrishi.app.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.data.FirebaseManager
import com.bharatkrishi.app.utils.GPSLocationManager
import com.bharatkrishi.app.utils.LocalizationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisasterReportScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseManager = remember { FirebaseManager() }

    var damageType by remember { mutableStateOf("Flood") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Auto-Detecting...") }
    var capturedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Camera & GPS
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { 
        if (it != null) capturedBitmap = it 
    }
    val gpsManager = remember { GPSLocationManager(context) }
    
    LaunchedEffect(Unit) {
        val loc = gpsManager.getCurrentLocation()
        location = if (loc != null) "${loc.latitude}, ${loc.longitude}" else "GPS Unavailable"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Disaster Report"), 
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = LocalizationManager.get("Report crop damage for insurance claim"),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Damage Type Dropdown (Simplified as Radio/Text for now)
            Text(LocalizationManager.get("Select Damage Type"), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Flood", "Drought", "Pest", "Fire").forEach { type ->
                    FilterChip(
                        selected = damageType == type,
                        onClick = { damageType = type },
                        label = { Text(type) }
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(LocalizationManager.get("Description of Damage")) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 5
            )

            // Photo Capture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { cameraLauncher.launch(null) }
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (capturedBitmap != null) {
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "Damage Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(LocalizationManager.get("Take photo of damage"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (description.isNotEmpty()) {
                        scope.launch {
                            isSubmitting = true
                            firebaseManager.saveDisasterReport(
                                userId = "test_user_001",
                                damageType = damageType,
                                description = description,
                                imageUrl = "dummy_url_claim",
                                location = location
                            )
                            isSubmitting = false
                            Toast.makeText(context, "Claim Submitted for Review", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                 if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(LocalizationManager.get("Submit Claim"))
                }
            }
        }
    }
}
