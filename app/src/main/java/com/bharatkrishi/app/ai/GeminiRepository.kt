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

    suspend fun sendMessage(userText: String, image: android.graphics.Bitmap? = null): String {
        // Prepend system prompt to user message for v1/gemini-pro compatibility
        val fullText = if (userText.isNotBlank()) "$systemPrompt\n\nUser Query: $userText" else systemPrompt

        val parts = mutableListOf<GeminiPart>()
        
        if (userText.isNotBlank()) {
            parts.add(GeminiPart(text = fullText))
        }

        if (image != null) {
            val base64Image = bitmapToBase64(image)
            parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Image)))
        }
        
        // If only image is sent, we should still send a text prompt to guide the model
        if (parts.isEmpty()) {
             // Fallback if somehow both are empty, though UI should prevent this
             parts.add(GeminiPart(text = "Analyze this image for agricultural insights."))
        }

        val userContent = GeminiContent(
            role = "user",
            parts = parts
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
        } catch (e: Throwable) {
            e.printStackTrace()
            return when (e) {
                is HttpException -> {
                    val errorBody = e.response()?.errorBody()?.string() ?: "No details"
                    if (e.code() == 429) "System busy ($errorBody). Please wait." 
                    else "Server error (${e.code()}): $errorBody"
                }
                is IOException -> "Network error. Please check your internet connection."
                else -> "An error occurred: ${e.localizedMessage}"
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

    private suspend fun bitmapToBase64(bitmap: android.graphics.Bitmap): String = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        // Resize if too big
        val scaledBitmap = if (bitmap.width > 512 || bitmap.height > 512) {
             val scale = 512.0f / kotlin.math.max(bitmap.width, bitmap.height)
             android.graphics.Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
        } else {
             bitmap
        }
        scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
    }

    private suspend fun <T> retryIO(
        times: Int = 2,
        initialDelay: Long = 1000, // 1 second
        maxDelay: Long = 10000,    // 10 seconds
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
