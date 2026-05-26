package com.aimodelaggregator.domain.models

data class ChatConversation(
    val id: Long = 0,
    val title: String,
    val provider: ProviderType,
    val modelId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val draftMessage: String? = null
)

data class ChatConversationWithMessages(
    val conversation: ChatConversation,
    val messages: List<ChatMessage>
)
