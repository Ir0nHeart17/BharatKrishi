package com.bharatkrishi.app.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bharatkrishi.app.WeatherViewModel
import com.bharatkrishi.app.network.NetworkResponse
import com.bharatkrishi.app.network.WeatherModel
import com.bharatkrishi.app.utils.LocalizationManager

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherForecastScreen(navController: NavController, viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    val weatherResult by viewModel.weatherResult.observeAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Weather Forecast"), 
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    
                    TextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = { Text(LocalizationManager.get("Search City (e.g. Pune)")) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = { viewModel.getData(city) },
                        modifier = Modifier.padding(end = 4.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(LocalizationManager.get("Search"))
                    }
                }
            }

            // Weather Content
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                when (val result = weatherResult) {
                    is NetworkResponse.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${LocalizationManager.get("Could not load weather.")}\n${result.message}",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is NetworkResponse.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is NetworkResponse.Success -> {
                        WeatherDetails(data = result.data)
                    }

                    null -> {
                         Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(
                                LocalizationManager.get("Enter a location to see the forecast"),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetails(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card (Location & Main Condition)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${data.location.name}, ${data.location.country}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${data.current.temp_c}°",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = data.current.condition.text,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    
                    AsyncImage(
                        modifier = Modifier.size(100.dp),
                        model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
                        contentDescription = "Condition Icon",
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${LocalizationManager.get("Feels Like:")} ${data.current.feelslike_c}°", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("${LocalizationManager.get("Last Updated:")} ${data.current.last_updated.split(" ")[1]}", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.7f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // 2. Weather Details Grid
        Text(
            LocalizationManager.get("Current Details"), 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Row 1
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WeatherMetricCard(
                    title = LocalizationManager.get("Humidity"),
                    value = "${data.current.humidity}%",
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricCard(
                    title = LocalizationManager.get("Wind"),
                    value = "${data.current.wind_kph} km/h",
                    subValue = data.current.wind_dir,
                    icon = Icons.Default.Air,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Row 2
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WeatherMetricCard(
                    title = LocalizationManager.get("UV Index"),
                    value = data.current.uv.toString(),
                    icon = Icons.Default.WbSunny,
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricCard(
                    title = LocalizationManager.get("Visibility"),
                    value = "${data.current.vis_km} km",
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
            }
            
             // Row 3 (Pressure & Precip)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WeatherMetricCard(
                    title = LocalizationManager.get("Pressure"),
                    value = "${data.current.pressure_mb} mb",
                    icon = Icons.Default.Speed, // Abstract gauge
                    modifier = Modifier.weight(1f)
                )
                WeatherMetricCard(
                    title = LocalizationManager.get("Precipitation"),
                    value = "${data.current.precip_mm} mm",
                    icon = Icons.Default.Cloud,
                    modifier = Modifier.weight(1f)
                )
            }
            
             // Row 4 (Cloud & Cloud Cover)
             Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                 WeatherMetricCard(
                    title = LocalizationManager.get("Cloud Cover"),
                    value = "${data.current.cloud}%",
                    icon = Icons.Default.CloudQueue,
                    modifier = Modifier.weight(1f)
                )
                 // Placeholder or extended info
                 Spacer(modifier = Modifier.weight(1f))
             }
        }
        
        Spacer(Modifier.height(80.dp)) // Bottom padding
    }
}

@Composable
fun WeatherMetricCard(
    title: String,
    value: String,
    subValue: String? = null,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        // border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subValue != null) {
                    Text(
                        text = subValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}