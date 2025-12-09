package com.bharatkrishi.app.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// Request Models
data class GeminiRequest(
    val contents: List<List_Content>,
    val systemInstruction: SystemInstruction? = null,
    val generationConfig: GenerationConfig? = null
)

data class SystemInstruction(
    val parts: List<Part>
)

data class List_Content(
    val parts: List<Part>,
    val role: String = "user"
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f
)

// Response Models
data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ContentResponse?,
    val finishReason: String?
)

data class ContentResponse(
    val parts: List<Part>?,
    val role: String?
)

// Retrofit Service
interface GeminiApiService {
    @POST("v1/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// Singleton Client
object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}
