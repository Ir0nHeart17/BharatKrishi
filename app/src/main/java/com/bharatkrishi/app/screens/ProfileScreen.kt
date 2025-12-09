// screens/ProfileScreen.kt
package com.bharatkrishi.app.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bharatkrishi.app.data.FirebaseManager
import com.bharatkrishi.app.data.UserProfile
import com.bharatkrishi.app.utils.GPSLocationManager
import com.bharatkrishi.app.utils.LocationUtils
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// ... (imports remain)
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseManager = remember { FirebaseManager() }
    
    // User ID (Should come from AuthViewModel)
    val userId = "test_user_001" 

    var showEditDialog by remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf(UserProfile(id = userId)) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch Profile
    LaunchedEffect(Unit) {
        val profile = firebaseManager.getUserProfile(userId)
        if (profile != null) {
            userProfile = profile
        }
        isLoading = false
    }

    // Location Logic
    val gpsManager = remember { GPSLocationManager(context) }
    var isDetectingLocation by remember { mutableStateOf(false) }
    
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
                        // Update state and immediate save if needed, or just local state
                        val updatedProfile = userProfile.copy(
                            village = address.village,
                            district = address.district,
                            state = address.state
                        )
                        userProfile = updatedProfile
                        firebaseManager.saveUserProfile(updatedProfile) // Save location immediately
                        Toast.makeText(context, "Location updated: ${address.village}", Toast.LENGTH_SHORT).show()
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

    if (showEditDialog) {
        EditProfileDialog(
            currentProfile = userProfile,
            onDismiss = { showEditDialog = false },
            onSave = { updatedProfile ->
                scope.launch {
                    firebaseManager.saveUserProfile(updatedProfile)
                    userProfile = updatedProfile
                    showEditDialog = false
                    Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "My Profile", 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    TextButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(4.dp))
                        Text("Edit", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Profile Header & Picture
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            userProfile.name.ifEmpty { "Farmer Name" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            userProfile.phone.ifEmpty { "+91 XXXXXXXXXX" },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    // Location Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Farm Location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        if(userProfile.village.isNotEmpty()) "${userProfile.village}, ${userProfile.district}" else "Location not set",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if(userProfile.state.isNotEmpty()) {
                                    Text(userProfile.state, fontSize = 12.sp, modifier = Modifier.padding(start = 20.dp))
                                }
                            }
                            
                            IconButton(
                                onClick = { 
                                    locationPermissionLauncher.launch(
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    )
                                }
                            ) {
                                if(isDetectingLocation) CircularProgressIndicator(Modifier.size(24.dp))
                                else Icon(Icons.Default.MyLocation, "Detect Location", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                item {
                    // Info Grid
                    Text("Farm Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailItem("Farm Size", userProfile.farmSize.ifEmpty { "Not set" }, Icons.Default.Landscape)
                        DetailItem("Soil Type", userProfile.soilType.ifEmpty { "Not set" }, Icons.Default.Terrain)
                        DetailItem("Primary Crops", userProfile.mainCrops.ifEmpty { "Not set" }, Icons.Default.Agriculture)
                        DetailItem("Experience", userProfile.experience.ifEmpty { "Not set" }, Icons.Default.Timeline)
                    }
                }
                
                item {
                     // Disclaimer / Info
                     Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(top=16.dp)
                     ) {
                         Row(Modifier.padding(12.dp)) {
                             Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                             Spacer(Modifier.width(8.dp))
                             Text(
                                 "This information helps our AI provide better crop advisory and pest control suggestions specific to your land.",
                                 fontSize = 12.sp,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant
                             )
                         }
                     }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).padding(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var location by remember { mutableStateOf(currentProfile.village) } // Just for display reference if needed
    var farmSize by remember { mutableStateOf(currentProfile.farmSize) }
    var soilType by remember { mutableStateOf(currentProfile.soilType) }
    var mainCrops by remember { mutableStateOf(currentProfile.mainCrops) }
    var experience by remember { mutableStateOf(currentProfile.experience) }
    var phone by remember { mutableStateOf(currentProfile.phone) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Edit Farmer Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Divider()
                
                OutlinedTextField(
                    value = farmSize,
                    onValueChange = { farmSize = it },
                    label = { Text("Farm Size (e.g., 2 Acres)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = soilType,
                    onValueChange = { soilType = it },
                    label = { Text("Soil Type (e.g., Loamy, Clay)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mainCrops,
                    onValueChange = { mainCrops = it },
                    label = { Text("Crops Grown (e.g., Wheat, Rice)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Farming Experience (Years)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                currentProfile.copy(
                                    name = name,
                                    phone = phone,
                                    farmSize = farmSize,
                                    soilType = soilType,
                                    mainCrops = mainCrops,
                                    experience = experience
                                )
                            )
                        }
                    ) {
                        Text("Save Details")
                    }
                }
            }
        }
    }
}
