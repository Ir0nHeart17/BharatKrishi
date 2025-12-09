package com.bharatkrishi.app.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.AuthViewModel
import com.bharatkrishi.app.data.FirebaseManager
import com.bharatkrishi.app.data.UserProfile
import com.bharatkrishi.app.utils.GPSLocationManager
import com.bharatkrishi.app.utils.LocationUtils
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCompletionScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseManager = remember { FirebaseManager() }
    
    // Get real user ID from AuthViewModel
    val currentUser by authViewModel.user.observeAsState()
    val userId = currentUser?.uid ?: "" 
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var mainCrops by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    
    // Location State
    var detectedVillage by remember { mutableStateOf("") }
    var detectedDistrict by remember { mutableStateOf("") }
    var detectedState by remember { mutableStateOf("") }
    var isDetectingLocation by remember { mutableStateOf(false) }

    val gpsManager = remember { GPSLocationManager(context) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            isDetectingLocation = true
            scope.launch {
                val loc = gpsManager.getCurrentLocation()
                if (loc != null) {
                    val address = LocationUtils.getAddressFromLatLng(context, LatLng(loc.latitude, loc.longitude))
                    if (address.error == null) {
                        detectedVillage = address.village
                        detectedDistrict = address.district
                        detectedState = address.state
                        Toast.makeText(context, "Location Found: ${address.village}", Toast.LENGTH_SHORT).show()
                    } else {
                         Toast.makeText(context, address.error, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Could not fetch GPS", Toast.LENGTH_SHORT).show()
                }
                isDetectingLocation = false
            }
        }
    }

    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                "Complete Your Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Tell us about yourself to get personalized crop advisory.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Progress Indicator (Visual)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
               Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
               Spacer(Modifier.width(8.dp))
               Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
               Spacer(Modifier.width(8.dp))
               Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha=0.3f)))
            }

            // Form
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Auto-Detect Location Button
                    OutlinedButton(
                        onClick = { 
                            locationPermissionLauncher.launch(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                         if (isDetectingLocation) {
                             CircularProgressIndicator(modifier = Modifier.size(16.dp))
                             Spacer(Modifier.width(8.dp))
                             Text("Detecting...")
                         } else if (detectedVillage.isNotEmpty()) {
                             Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50))
                             Spacer(Modifier.width(8.dp))
                             Text("$detectedVillage, $detectedDistrict", color = Color(0xFF4CAF50))
                         } else {
                             Icon(Icons.Default.MyLocation, null)
                             Spacer(Modifier.width(8.dp))
                             Text("Auto-Detect Farm Location")
                         }
                    }

                    Divider()

                    OutlinedTextField(
                        value = farmSize,
                        onValueChange = { farmSize = it },
                        label = { Text("Farm Size (Acres)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = soilType,
                        onValueChange = { soilType = it },
                        label = { Text("Soil Type") },
                        modifier = Modifier.fillMaxWidth()
                    )

                     OutlinedTextField(
                        value = mainCrops,
                        onValueChange = { mainCrops = it },
                        label = { Text("Main Crops") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("Experience (Years)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        scope.launch {
                            isSaving = true
                            val profile = UserProfile(
                                id = userId,
                                name = name,
                                phone = phone,
                                village = detectedVillage,
                                district = detectedDistrict,
                                state = detectedState,
                                farmSize = farmSize,
                                soilType = soilType,
                                mainCrops = mainCrops,
                                experience = experience
                            )
                            firebaseManager.saveUserProfile(profile)
                            isSaving = false
                            
                            // Navigate to Home
                            navController.navigate("home") {
                                popUpTo("profile_completion") { inclusive = true }
                                popUpTo("signup") { inclusive = true } // Clear backstack
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSaving
            ) {
                if(isSaving) CircularProgressIndicator(color = Color.White)
                else Text("Save Profile & Continue", fontSize = 16.sp)
            }
        }
    }
}
