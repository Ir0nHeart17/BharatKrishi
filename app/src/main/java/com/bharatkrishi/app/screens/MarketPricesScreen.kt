package com.bharatkrishi.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.DataState
import com.bharatkrishi.app.MarketData
import com.bharatkrishi.app.MarketViewModel
import com.bharatkrishi.app.utils.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPricesScreen(navController: NavController, marketViewModel: MarketViewModel) {
    // Observe the live data from the ViewModel
    val state by marketViewModel.marketDataState.observeAsState()

    // Observe the selected filter values from the ViewModel to update the UI
    val selectedState by marketViewModel.selectedState.observeAsState()
    val selectedCommodity by marketViewModel.selectedCommodity.observeAsState()

    // Sample lists for our dropdown menus.
    // Localize the hardcoded lists too or handle in onOptionSelected? 
    // Easier to just use English keys for logic and display localized versions in UI? 
    // Plan: keep logic values in English if API expects English. Assuming API expects "Wheat", "Onion" etc.
    // We will display Localized values in Dropdown, but select English value for ViewModel if strictly needed.
    // For simplicity given the request, let's assume ViewModel handles string matching or is flexible. 
    // Actually, let's keep the lists simple but localized.
    
    // Raw lists for API (English)
    val rawStates = listOf("All States", "Maharashtra", "Punjab", "Uttar Pradesh", "Andhra Pradesh", "Gujarat")
    val rawCommodities = listOf("All Commodities", "Wheat")

    // Localized lists for Display
    val states = rawStates.map { LocalizationManager.get(it) }
    val commodities = rawCommodities.map { LocalizationManager.get(it) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        LocalizationManager.get("Market Prices"), 
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
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            // UI FOR FILTER DROPDOWNS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // State Dropdown
                FilterDropdown(
                    label = LocalizationManager.get("State"),
                    options = states,
                    selectedOption = LocalizationManager.get(selectedState ?: "All States"),
                    onOptionSelected = { selected ->
                        // Reverse lookup to find the English value
                        val englishValue = rawStates.firstOrNull { LocalizationManager.get(it) == selected } ?: selected
                        
                        // Handle "All States" case -> null
                        val valueToSet = if (englishValue == "All States") null else englishValue
                        
                        marketViewModel.onStateSelected(valueToSet)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Commodity Dropdown
                FilterDropdown(
                    label = LocalizationManager.get("Commodity"),
                    options = commodities,
                    selectedOption = LocalizationManager.get(selectedCommodity ?: "All Commodities"),
                    onOptionSelected = { selected ->
                        // Reverse lookup to find the English value
                        val englishValue = rawCommodities.firstOrNull { LocalizationManager.get(it) == selected } ?: selected

                        // Handle "All Commodities" case -> null
                        val valueToSet = if (englishValue == "All Commodities") null else englishValue
                        
                        marketViewModel.onCommoditySelected(valueToSet)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Box for displaying Loading/Success/Error states
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val currentState = state) {
                    is DataState.Loading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    is DataState.Success -> {
                        if (currentState.data.isEmpty()) {
                            Text(LocalizationManager.get("No results found for this filter."), color = MaterialTheme.colorScheme.onSurface)
                        } else {
                            MarketDataList(marketList = currentState.data)
                        }
                    }
                    is DataState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Text("${LocalizationManager.get("Error")}: ${currentState.message}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { marketViewModel.retry() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                    null -> {
                        // Initial empty state, you can put a prompt here
                        Text(LocalizationManager.get("Select filters to see prices."), color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

// NEW, REUSABLE DROPDOWN COMPONENT
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Composable to display the full list of market data
@Composable
fun MarketDataList(marketList: List<MarketData>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(marketList) { marketData ->
            ApiMarketCropCard(data = marketData)
        }
    }
}

// Composable for a single item in the detailed list
@Composable
fun ApiMarketCropCard(data: MarketData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Commodity and Market details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    LocalizationManager.get(data.commodity ?: "Unknown Commodity"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Market: ${data.market}, ${data.district}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Right side: Price details
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "₹${data.modal_price}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF2E7D32) // Use semantic green for price
                )
                Text(
                    "${LocalizationManager.get("Min")}: ₹${data.min_price} | ${LocalizationManager.get("Max")}: ₹${data.max_price}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}