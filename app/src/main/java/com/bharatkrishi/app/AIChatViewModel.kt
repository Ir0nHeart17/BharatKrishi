package com.bharatkrishi.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatkrishi.app.ai.GeminiApiClient
import com.bharatkrishi.app.ai.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Simple manual DI
    private val repository = GeminiRepository(GeminiApiClient.api)

    fun sendMessage(text: String) {
        val userMessage = ChatMessage(text, true, "Now")
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            try {
                val reply = repository.sendMessage(text)

                val aiMessage = ChatMessage(
                    text = reply,
                    isFromUser = false,
                    timestamp = "Now"
                )

                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    text = "Error: ${e.message}",
                    isFromUser = false,
                    timestamp = "Now"
                )
                _messages.value = _messages.value + errorMessage
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)
