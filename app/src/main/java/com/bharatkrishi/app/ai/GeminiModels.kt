package com.bharatkrishi.app.ai

//REQUEST MODELS deta ha
data class GeminiPart(
    val text: String
)

data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

data class GeminiGenerateContentRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

//RESPONSE MODELS
data class GeminiGenerateContentResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)
