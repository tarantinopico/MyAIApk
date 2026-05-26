package com.aimodelaggregator.domain.models

data class ChatMessage(
    val id: Long = 0,
    val conversationId: Long,
    val role: MessageRole,
    val content: String,
    val isStreaming: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val tokenCount: Int? = null
)
