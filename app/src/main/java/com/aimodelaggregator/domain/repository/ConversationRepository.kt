package com.aimodelaggregator.domain.repository

import com.aimodelaggregator.domain.models.ChatConversation
import com.aimodelaggregator.domain.models.ChatConversationWithMessages
import com.aimodelaggregator.domain.models.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAllConversations(): Flow<List<ChatConversation>>
    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessage>>
    suspend fun saveConversation(conversation: ChatConversation): Long
    suspend fun createConversation(conversation: ChatConversation): Long
    suspend fun updateConversation(conversation: ChatConversation)
    suspend fun updateConversationTitle(id: Long, title: String)
    suspend fun deleteConversation(conversation: ChatConversation)
    suspend fun deleteConversation(id: Long)
    suspend fun saveMessage(message: ChatMessage): Long
    suspend fun addMessage(message: ChatMessage): Long
    suspend fun updateMessage(message: ChatMessage)
    suspend fun deleteMessage(message: ChatMessage)
    suspend fun getConversationWithMessagesSync(conversationId: Long): ChatConversationWithMessages?
}
