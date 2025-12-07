package com.bharatkrishi.app.ai

import com.bharatkrishi.app.BuildConfig
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

class GeminiRepository(
    private val api: GeminiApiService
) {
    private val systemPrompt = """
        You are "Krishi Mitra", a friendly, expert agricultural AI assistant inside the BharatKrishi app.
        You help Indian farmers by giving simple, accurate, practical advice in Hindi or Hinglish.
        Tone: Supportive, humble, like a village farming advisor.

        Responsibilities:
        - Wheat disease detection (Yellow/Brown/Stripe rust, Loose smut, Septoria, Mildew).
        - Drone field analysis & Soil health.
        - Weather-based advisory & Market updates.
        - Pest & fertilizer guidance (Safe, govt-approved only).

        Voice Instructions:
        - Respond as if speaking Hindi.
        - Keep sentences clean for Text-to-Speech.
        - Avoid English words unless necessary.

        Creator: If asked who created this app, respond with "ADITYA RAKESH SHARMA".
        Never give illegal/harmful advice or medical advice.
    """.trimIndent()

    suspend fun sendMessage(userText: String): String {
        // Prepend system prompt to user message for v1/gemini-pro compatibility
        val fullText = "$systemPrompt\n\nUser Query: $userText"

        val userContent = GeminiContent(
            role = "user",
            parts = listOf(GeminiPart(fullText))
        )

        val request = GeminiGenerateContentRequest(
            contents = listOf(userContent),
            systemInstruction = null // Not supported in v1 gemini-pro
        )

        val response = try {
            retryIO {
                api.generateContent(
                    request = request,
                    apiKey = BuildConfig.GEMINI_API_KEY
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return when (e) {
                is HttpException -> if (e.code() == 429) "System busy (Too Many Requests). Please try again in a moment." else "Server error: ${e.message()}"
                is IOException -> "Network error. Please check your internet connection."
                else -> "An error occurred. Please try again."
            }
        }

        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: "Maaf kijiye, samajh nahi aaya."
    }

    private suspend fun <T> retryIO(
        times: Int = 3,
        initialDelay: Long = 1000, // 1 second
        maxDelay: Long = 5000,    // 5 seconds
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: Exception) {
                // Only retry on Network errors (IOException) or Too Many Requests (429)
                val shouldRetry = e is IOException || (e is HttpException && e.code() == 429)
                if (!shouldRetry) throw e
                
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
        return block() // Last attempt
    }
}
