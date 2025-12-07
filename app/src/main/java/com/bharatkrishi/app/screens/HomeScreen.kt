package com.bharatkrishi.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.AuthViewModel
import com.bharatkrishi.app.MarketViewModel
import com.bharatkrishi.app.R
import com.bharatkrishi.app.WeatherViewModel
import com.bharatkrishi.app.network.NetworkResponse
import com.bharatkrishi.app.ui.theme.BharatKrishiGreen
import com.bharatkrishi.app.ui.theme.BharatKrishiOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    marketViewModel: MarketViewModel,
    weatherViewModel: WeatherViewModel,
    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedLanguage by remember { mutableStateOf("English") }
    
    val user by authViewModel.user.observeAsState()
    val username = user?.displayName ?: user?.email?.substringBefore("@") ?: "Farmer"

    val weatherResult by weatherViewModel.weatherResult.observeAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.getData("New Delhi")
    }

    val quickActions = listOf(
        QuickAction(stringResource(R.string.soil_analysis), Icons.Default.Biotech, "soil_info"),
        QuickAction(stringResource(R.string.ai_chat), Icons.Default.SmartToy, "ai_chat"),
        QuickAction(stringResource(R.string.drone_analysis), Icons.Default.AirplanemodeActive, "drone_analysis"),
        QuickAction(stringResource(R.string.community_forum), Icons.Default.People, "community_forum")
    )

    val govSchemes = listOf(
        GovScheme("PM Kisan Samman Nidhi", "₹6000/year support"),
        GovScheme("Fasal Bima Yojana", "Crop Insurance"),
        GovScheme("Kisan Credit Card", "Low interest loans"),
        GovScheme("Soil Health Card", "Soil testing scheme")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                // User Profile Header in Drawer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BharatKrishiGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = user?.email ?: "", fontSize = 14.sp, color = Color.Gray)
                }
                
                Divider()
                
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.home)) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.pest_control)) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("pest_control")
                    },
                    icon = { Icon(Icons.Default.BugReport, null) }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.fertilizer_advisory)) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("fertilizer_advisory")
                    },
                    icon = { Icon(Icons.Default.Grass, null) }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.gps_location)) },
                    selected = false,
                    onClick = { /* TODO: Toggle GPS */ },
                    icon = { Icon(Icons.Default.LocationOn, null) }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.logout)) },
                    selected = false,
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, null) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            color = BharatKrishiGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("notifications") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // WELCOME CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BharatKrishiGreen)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            stringResource(R.string.welcome_farmer),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            username,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // GOV SCHEMES
                Text(
                    stringResource(R.string.gov_schemes),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(govSchemes) { scheme ->
                        GovSchemeCard(scheme)
                    }
                }

                // MARKET PRICE PREVIEW
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate("market_prices")
                    }
                ) {
                    MarketPricePreview(marketViewModel)
                }

                // WEATHER BLOCK
                when (val result = weatherResult) {
                    is NetworkResponse.Success -> {
                        WeatherAlertCard(
                            temperature = "${result.data.current.temp_c.toInt()}°C",
                            description = result.data.current.condition.text,
                            onClick = { navController.navigate("weather_page") }
                        )
                    }
                    is NetworkResponse.Loading -> {
                        WeatherAlertCard(
                            temperature = "--°C",
                            description = "Loading...",
                            onClick = {}
                        )
                    }
                    is NetworkResponse.Error -> {
                        WeatherAlertCard(
                            temperature = "!",
                            description = "Error",
                            onClick = { weatherViewModel.getData("Delhi") }
                        )
                    }
                    null -> {
                        WeatherAlertCard(
                            temperature = "--°C",
                            description = "Fetching...",
                            onClick = {}
                        )
                    }
                }

                // QUICK ACTIONS
                Text(
                    "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                QuickActionsGrid(navController, quickActions)
            }
        }
    }
}

@Composable
fun GovSchemeCard(scheme: GovScheme) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = scheme.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = BharatKrishiGreen,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = scheme.benefit,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

data class GovScheme(val name: String, val benefit: String)

// ... (Keep existing QuickActionsGrid, QuickActionCard, WeatherAlertCard, BottomNavigationBar, data classes)
// I will append the rest of the file content here to ensure nothing is lost.

@Composable
fun QuickActionsGrid(navController: NavController, quickActions: List<QuickAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (quickActions.isNotEmpty()) {
                QuickActionCard(quickActions[0], Modifier.weight(1f)) {
                    navController.navigate(quickActions[0].route)
                }
            }
            if (quickActions.size > 1) {
                QuickActionCard(quickActions[1], Modifier.weight(1f)) {
                    navController.navigate(quickActions[1].route)
                }
            }
        }
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (quickActions.size > 2) {
                QuickActionCard(quickActions[2], Modifier.weight(1f)) {
                    navController.navigate(quickActions[2].route)
                }
            }
            if (quickActions.size > 3) {
                QuickActionCard(quickActions[3], Modifier.weight(1f)) {
                    navController.navigate(quickActions[3].route)
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, contentDescription = action.title, tint = BharatKrishiGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Text(action.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}

@Composable
fun WeatherAlertCard(temperature: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Cloud, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(stringResource(R.string.weather_alert), fontWeight = FontWeight.Medium)
                Text("$temperature • $description", color = Color(0xFF1976D2))
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF1976D2))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            BottomNavItem(stringResource(R.string.home), Icons.Default.Home, "home"),
            BottomNavItem(stringResource(R.string.crop_advisory), Icons.Default.Agriculture, "crop_advisory"),
            BottomNavItem(stringResource(R.string.help), Icons.Default.Help, "help_support"),
            BottomNavItem(stringResource(R.string.profile), Icons.Default.Person, "profile")
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, fontSize = 10.sp) },
                selected = item.route == "home",
                onClick = { navController.navigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BharatKrishiGreen,
                    selectedTextColor = BharatKrishiGreen,
                    indicatorColor = BharatKrishiGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class QuickAction(val title: String, val icon: ImageVector, val route: String)
data class BottomNavItem(val title: String, val icon: ImageVector, val route: String)
