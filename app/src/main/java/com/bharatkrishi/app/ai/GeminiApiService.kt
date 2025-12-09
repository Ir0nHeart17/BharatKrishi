package com.bharatkrishi.app.ai

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {

    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Body request: GeminiGenerateContentRequest,
        @Query("key") apiKey: String
    ): GeminiGenerateContentResponse
}

