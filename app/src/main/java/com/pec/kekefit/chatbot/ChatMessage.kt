package com.pec.kekefit.chatbot

data class ChatMessage(
    val role: String,
    val content: String,
    val isUser: Boolean = role == "user",
    val timestamp: Long = System.currentTimeMillis()
)
