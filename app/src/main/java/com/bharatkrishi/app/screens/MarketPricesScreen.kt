package com.bharatkrishi.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.DataState
import com.bharatkrishi.app.MarketData
import com.bharatkrishi.app.MarketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPricesScreen(navController: NavController, marketViewModel: MarketViewModel) {
    // Observe the live data from the ViewModel
    val state by marketViewModel.marketDataState.observeAsState()

    // Observe the selected filter values from the ViewModel to update the UI
    val selectedState by marketViewModel.selectedState.observeAsState()
    val selectedCommodity by marketViewModel.selectedCommodity.observeAsState()

    // Sample lists for our dropdown menus.
    val states = listOf("All States", "Maharashtra", "Punjab", "Uttar Pradesh", "Andhra Pradesh", "Gujarat")
    val commodities = listOf("All Commodities", "Onion", "Potato", "Tomato", "Wheat", "Cotton", "Lemon")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Market Price List", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            // --- UI FOR FILTER DROPDOWNS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // State Dropdown
                FilterDropdown(
                    label = "State",
                    options = states,
                    selectedOption = selectedState ?: "All States",
                    onOptionSelected = { selected ->
                        // If "All States" is chosen, we pass null to the ViewModel to remove the filter.
                        // Otherwise, we pass the selected state name.
                        val valueToSet = if (selected == "All States") null else selected
                        marketViewModel.onStateSelected(valueToSet)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Commodity Dropdown
                FilterDropdown(
                    label = "Commodity",
                    options = commodities,
                    selectedOption = selectedCommodity ?: "All Commodities",
                    onOptionSelected = { selected ->
                        val valueToSet = if (selected == "All Commodities") null else selected
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
                        CircularProgressIndicator()
                    }
                    is DataState.Success -> {
                        if (currentState.data.isEmpty()) {
                            Text("No results found for this filter.")
                        } else {
                            MarketDataList(marketList = currentState.data)
                        }
                    }
                    is DataState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Text("Error: ${currentState.message}", color = Color.Red, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { marketViewModel.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                    null -> {
                        // Initial empty state, you can put a prompt here
                        Text("Select filters to see prices.")
                    }
                }
            }
        }
    }
}

// --- NEW, REUSABLE DROPDOWN COMPONENT ---
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
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
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
        verticalArrangement = Arrangement.spacedBy(12.dp) // Add some space between items
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    data.commodity ?: "Unknown Commodity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "Market: ${data.market}, ${data.district}",
                    fontSize = 12.sp,
                    color = Color.Gray
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
                    color = Color(0xFF2E7D32) // Use app theme color
                )
                Text(
                    "Min: ₹${data.min_price} | Max: ₹${data.max_price}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}