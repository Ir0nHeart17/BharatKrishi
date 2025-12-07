package com.bharatkrishi.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bharatkrishi.app.screens.*
import com.bharatkrishi.app.ui.theme.BharatKrishiTheme

// --- MERGED AND CORRECTED MAIN ACTIVITY ---
class MainActivity : ComponentActivity() {

    // 1. Get a reference to the ViewModel using the standard delegate for Compose activities
    private val marketViewModel: MarketViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Start fetching data as soon as the activity is created
        marketViewModel.fetchMarketData()

        setContent {
            BharatKrishiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // You can pass the ViewModel down to your composable if needed
                    BharatKrishiApp(marketViewModel, weatherViewModel, authViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BharatKrishiApp(
    marketViewModel: MarketViewModel,
    weatherViewModel: WeatherViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.user.observeAsState()

    // Determine start destination based on auth state
    val startDestination = if (currentUser != null) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }
        
        composable("home") { HomeScreen(navController, marketViewModel, weatherViewModel, authViewModel) }
        composable("notifications") { NotificationsScreen(navController) }
        // Example: Pass the ViewModel to the screen that will display the data
        composable("market_prices") { MarketPricesScreen(navController, marketViewModel) }
        composable("soil_info") { SoilInfoScreen(navController) }
        composable("ai_chat") { AIChatScreen(navController) }
        composable("weather_page") { WeatherForecastScreen(navController, weatherViewModel) }
        composable("fertilizer_advisory") { FertilizerAdvisoryScreen(navController) }
        composable("crop_advisory") { CropAdvisoryScreen(navController) }
        composable("pest_control") { PestControlScreen(navController) }
        composable("help_support") { HelpSupportScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("youtube_tutorials") { YoutubeTutorialsScreen(navController) }
        composable("drone_analysis") {
            DroneAnalysisScreen(navController)
        }
        composable("community_forum") { CommunityForumScreen(navController) }

    }
}
