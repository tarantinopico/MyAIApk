package com.aimodelaggregator.data.repository

import com.aimodelaggregator.data.database.dao.ConversationDao
import com.aimodelaggregator.data.database.dao.MessageDao
import com.aimodelaggregator.data.mappers.toDomain
import com.aimodelaggregator.data.mappers.toEntity
import com.aimodelaggregator.domain.models.ChatConversation
import com.aimodelaggregator.domain.models.ChatMessage
import com.aimodelaggregator.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConversationRepositoryImpl(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ConversationRepository {

    override fun getAllConversations(): Flow<List<ChatConversation>> {
        return conversationDao.getAllConversations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveConversation(conversation: ChatConversation): Long {
        return conversationDao.insertConversation(conversation.toEntity())
    }

    override suspend fun createConversation(conversation: ChatConversation): Long {
        return saveConversation(conversation)
    }

    override suspend fun updateConversation(conversation: ChatConversation) {
        conversationDao.updateConversation(conversation.toEntity())
    }

    override suspend fun updateConversationTitle(id: Long, title: String) {
        conversationDao.updateConversationTitle(id = id, title = title)
    }

    override suspend fun deleteConversation(conversation: ChatConversation) {
        conversationDao.deleteConversation(conversation.toEntity())
    }

    override suspend fun deleteConversation(id: Long) {
        val conv = conversationDao.getConversationById(id)
        if (conv != null) {
            conversationDao.deleteConversation(conv)
        }
    }

    override suspend fun deleteAllConversations() {
        conversationDao.deleteAllConversations()
    }

    override suspend fun saveMessage(message: ChatMessage): Long {
        return messageDao.insertMessage(message.toEntity())
    }

    override suspend fun addMessage(message: ChatMessage): Long {
        return saveMessage(message)
    }

    override suspend fun updateMessage(message: ChatMessage) {
        messageDao.updateMessage(message.toEntity())
    }

    override suspend fun deleteMessage(message: ChatMessage) {
        messageDao.deleteMessage(message.toEntity())
    }

    override suspend fun getConversationWithMessagesSync(conversationId: Long): com.aimodelaggregator.domain.models.ChatConversationWithMessages? {
        val convEntity = conversationDao.getConversationById(conversationId) ?: return null
        val messageEntities = messageDao.getMessagesForConversationSync(conversationId)
        return com.aimodelaggregator.domain.models.ChatConversationWithMessages(
            conversation = convEntity.toDomain(),
            messages = messageEntities.map { it.toDomain() }
        )
    }
}
