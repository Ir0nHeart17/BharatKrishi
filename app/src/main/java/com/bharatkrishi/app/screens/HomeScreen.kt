package com.bharatkrishi.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import kotlinx.coroutines.delay
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.AuthViewModel
import com.bharatkrishi.app.MarketViewModel
import com.bharatkrishi.app.R
import com.bharatkrishi.app.WeatherViewModel
import com.bharatkrishi.app.network.NetworkResponse
import com.bharatkrishi.app.ui.theme.BharatKrishiGreen
import com.bharatkrishi.app.utils.LocalizationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
        QuickAction(LocalizationManager.get("Wheat Detection"), Icons.Default.Biotech, "soil_info"),
        QuickAction(LocalizationManager.get("AI Assistant"), Icons.Default.SmartToy, "ai_chat"),
        QuickAction(LocalizationManager.get("Drone Analysis"), Icons.Default.AirplanemodeActive, "drone_analysis"),
        QuickAction(LocalizationManager.get("Community Forum"), Icons.Default.People, "community_forum"),
        QuickAction(LocalizationManager.get("Crop Verification"), Icons.Default.Verified, "crop_registration"),
        QuickAction(LocalizationManager.get("Disaster Report"), Icons.Default.Warning, "disaster_report")
    )

    val govSchemes = listOf(
        GovScheme("PM Kisan Samman Nidhi", "₹6000/year support", R.drawable.img_gov_scheme_1),
        GovScheme("PM Kisan Scheme", "Crop Insurance", R.drawable.img_gov_scheme_2),
        GovScheme("Central Gov Schemes", "Low interest loans", R.drawable.img_gov_scheme_3),
        GovScheme("Soil Health Card", "Soil testing scheme", R.drawable.img_gov_scheme_3)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                // .. profile summary
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

                // Language Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (LocalizationManager.isHindi) "हिन्दी" else "English",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = LocalizationManager.isHindi,
                        onCheckedChange = { LocalizationManager.isHindi = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                Divider()
                
                NavigationDrawerItem(
                    label = { Text(LocalizationManager.get("Home")) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationDrawerItem(
                    label = { Text(LocalizationManager.get("Pest Control")) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("pest_control")
                    },
                    icon = { Icon(Icons.Default.BugReport, null) }
                )
                NavigationDrawerItem(
                    label = { Text(LocalizationManager.get("Fertilizer Advisory")) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("fertilizer_advisory")
                    },
                    icon = { Icon(Icons.Default.Grass, null) }
                )
                NavigationDrawerItem(
                    label = { Text(LocalizationManager.get("GPS Location")) },
                    selected = false,
                    onClick = { /* TODO: Toggle GPS */ },
                    icon = { Icon(Icons.Default.LocationOn, null) }
                )
                NavigationDrawerItem(
                    label = { Text(LocalizationManager.get("Help & Support")) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        // Navigate to Help screen - ensure route exists or create it
                        navController.navigate("help_support")
                    },
                    icon = { Icon(Icons.Default.Help, null) }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                NavigationDrawerItem(
                    label = { Text(LocalizationManager.get("Logout")) },
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
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("notifications") }) {
                            // Badge logic could go here
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
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
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // .. welcome card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            LocalizationManager.get("Good Morning, Farmer"),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            username,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // .. gov schemes
                Text(
                    LocalizationManager.get("Govt Schemes"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // .. auto scroll
                val pagerState = rememberPagerState(pageCount = { govSchemes.size })
                
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(3000)
                        val nextPage = (pagerState.currentPage + 1) % govSchemes.size
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
                
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    pageSpacing = 16.dp, 
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                ) { page ->
                    GovSchemeCard(govSchemes[page], Modifier.fillMaxWidth())
                }

                // .. market prices
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate("market_prices")
                    }
                ) {
                    MarketPricePreview(marketViewModel)
                }

                // .. weather info
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
                            description = LocalizationManager.get("Loading..."),
                            onClick = {}
                        )
                    }
                    is NetworkResponse.Error -> {
                        WeatherAlertCard(
                            temperature = "!",
                            description = LocalizationManager.get("Error"),
                            onClick = { weatherViewModel.getData("Delhi") }
                        )
                    }
                    null -> {
                        WeatherAlertCard(
                            temperature = "--°C",
                            description = LocalizationManager.get("Fetching..."),
                            onClick = {}
                        )
                    }
                }

                // .. quick actions
                Text(
                    LocalizationManager.get("Quick Actions"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                QuickActionsGrid(navController, quickActions)
            }
        }
    }
}

@Composable
fun GovSchemeCard(scheme: GovScheme, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = scheme.imageRes),
                contentDescription = scheme.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = scheme.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = scheme.benefit,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

data class GovScheme(val name: String, val benefit: String, val imageRes: Int)



@Composable
fun QuickActionsGrid(navController: NavController, quickActions: List<QuickAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        quickActions.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { action ->
                    QuickActionCard(action, Modifier.weight(1f)) {
                        navController.navigate(action.route)
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon color matches the theme
            Icon(action.icon, contentDescription = action.title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                action.title, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Medium, 
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeatherAlertCard(temperature: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Cloud, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(LocalizationManager.get("Weather Alert"), fontWeight = FontWeight.Medium)
                Text("$temperature • $description", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// Navigation bar logic is in a separate file

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)

