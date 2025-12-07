package com.bharatkrishi.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bharatkrishi.app.network.AgriApi
import com.bharatkrishi.app.network.AgriApiRecord
import kotlinx.coroutines.launch
import com.bharatkrishi.app.AppDatabase
import com.bharatkrishi.app.MarketData

// A simple sealed class to represent the state of the data loading process
sealed class DataState {
    object Loading : DataState()
    data class Success(val data: List<MarketData>) : DataState()
    data class Error(val message: String) : DataState()
}

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    // 1. DATABASE AND API SETUP
    private val apiService = AgriApi.retrofitService

    // 2. LIVE DATA TO OBSERVE FROM THE UI
    // The UI (your Activity/Fragment) will watch this to get updates
    private val _marketDataState = MutableLiveData<DataState>()
    val marketDataState: LiveData<DataState> = _marketDataState

    private val _selectedState = MutableLiveData<String?>()
    val selectedState: LiveData<String?> = _selectedState

    private val _selectedCommodity = MutableLiveData<String?>()
    val selectedCommodity: LiveData<String?> = _selectedCommodity

    // The UI will call these functions when the user selects from a dropdown.
    fun onStateSelected(state: String?) {
        _selectedState.value = state
        fetchMarketData() // Re-fetch data with the new filter
    }

    fun onCommoditySelected(commodity: String?) {
        _selectedCommodity.value = commodity
        fetchMarketData() // Re-fetch data with the new filter
    }

    fun retry() {
        fetchMarketData()
    }

    fun fetchMarketData() {
        viewModelScope.launch {
            _marketDataState.value = DataState.Loading
            fetchFromApi()
        }
    }

    private suspend fun fetchFromApi() {
        try {
            // 1. Create a map for our filters.
            val apiFilters = mutableMapOf<String, String>()

            // 2. Get the current user selections.
            val state = _selectedState.value
            val commodity = _selectedCommodity.value

            // 3. Add them to the map *only if* they are not null or blank.
            if (!state.isNullOrBlank()) {
                apiFilters["filters[state]"] = state
            }
            if (!commodity.isNullOrBlank()) {
                apiFilters["filters[commodity]"] = commodity
            }

            // 4. Make the network call with the filters map.
            val apiResponse = apiService.getMarketData(
                apiKey = "579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b",
                format = "json",
                offset = "0",
                limit = "100",
                filters = apiFilters // Pass the map here!
            )

            val newData = mapApiResponseToMarketData(apiResponse.records)
            if (newData.isEmpty()) {
                 _marketDataState.value = DataState.Error("No data found for selected filters.")
            } else {
                _marketDataState.value = DataState.Success(newData)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _marketDataState.value = DataState.Error("Network error: ${e.message}. Tap to retry.")
        }
    }

    /**
     * Helper function to convert the network data model (AgriApiRecord)
     * to our local database model (MarketData).
     */
    private fun mapApiResponseToMarketData(apiRecords: List<AgriApiRecord>): List<MarketData> {
        return apiRecords.map { record ->
            MarketData(
                state = record.state,
                district = record.district,
                market = record.market,
                commodity = record.commodity,
                variety = record.variety,
                min_price = record.min_price,
                max_price = record.max_price,
                modal_price = record.modal_price
                // The 'lastRefreshed' timestamp is automatically set by the MarketData class
            )
        }
    }
}