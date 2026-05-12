package com.pec.kekefit.chatbot

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<MessagePayload>,
    val max_tokens: Int = 600,
    val temperature: Double = 0.7
)

data class MessagePayload(
    val role: String,
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: MessagePayload
)

interface GroqApiService {
    @POST("openai/v1/chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") auth: String,
        @Body request: GroqRequest
    ): Response<GroqResponse>
}
