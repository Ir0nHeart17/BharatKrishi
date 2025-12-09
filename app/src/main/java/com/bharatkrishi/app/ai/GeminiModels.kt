package com.bharatkrishi.app.ai

import com.google.gson.annotations.SerializedName

//REQUEST MODELS deta ha
data class GeminiPart(
    val text: String? = null,
    @SerializedName("inline_data") val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    @SerializedName("mime_type") val mimeType: String,
    val data: String
)

data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

data class GeminiGenerateContentRequest(
    val contents: List<GeminiContent>,
    @SerializedName("system_instruction") val systemInstruction: GeminiContent? = null
)

//RESPONSE MODELS
data class GeminiGenerateContentResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)
