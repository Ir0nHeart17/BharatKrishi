package com.bharatkrishi.app.screens

import android.Manifest
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
fun CropRegistrationScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseManager = remember { FirebaseManager() }
    
    var cropName by remember { mutableStateOf("Wheat") }
    var area by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Auto-Detecting...") }
    var capturedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Camera Logic
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
        }
    }
    
    // GPS Logic
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
                        LocalizationManager.get("Crop Registration"), 
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
            // Title
            Text(
                text = LocalizationManager.get("Verify Your Sown Crop (e-Girdawari)"),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Form
            OutlinedTextField(
                value = cropName,
                onValueChange = { cropName = it },
                label = { Text(LocalizationManager.get("Crop Name")) },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text(LocalizationManager.get("Sown Area (Acres)")) },
                modifier = Modifier.fillMaxWidth()
            )

            // Location Read-only
            OutlinedTextField(
                value = location,
                onValueChange = {},
                label = { Text(LocalizationManager.get("GPS Location (Auto)")) },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) }
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
                        contentDescription = "Captured Crop",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(LocalizationManager.get("Tap to take photo of field"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = {
                    if (cropName.isNotEmpty() && area.isNotEmpty()) {
                        scope.launch {
                            isSubmitting = true
                            // In real app, upload bitmap to storage first, get URL
                            firebaseManager.saveCropRegistration(
                                userId = "test_user_001",
                                cropName = cropName,
                                area = area,
                                imageUrl = "dummy_url_for_demo", 
                                location = location
                            )
                            isSubmitting = false
                            Toast.makeText(context, "Registration Submitted!", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(LocalizationManager.get("Submit for Verification"))
                }
            }
        }
    }
}
