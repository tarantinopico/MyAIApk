package com.aimodelaggregator.domain.repository

import com.aimodelaggregator.domain.models.ChatConversation
import com.aimodelaggregator.domain.models.ChatMessage
import com.aimodelaggregator.domain.models.ProviderType
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(
        conversationId: Long,
        message: String,
        provider: ProviderType,
        modelId: String
    ): ChatMessage

    fun sendMessageStream(
        conversationId: Long,
        message: String,
        provider: ProviderType,
        modelId: String
    ): Flow<String>
}
