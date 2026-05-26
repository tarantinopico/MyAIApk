package com.aimodelaggregator.data.mappers

import com.aimodelaggregator.data.database.entity.ConversationEntity
import com.aimodelaggregator.data.database.entity.MessageEntity
import com.aimodelaggregator.data.database.entity.ProviderModelEntity
import com.aimodelaggregator.domain.models.ChatConversation
import com.aimodelaggregator.domain.models.ChatMessage
import com.aimodelaggregator.domain.models.ProviderModel

fun ProviderModelEntity.toDomain() = ProviderModel(
    id = id,
    provider = provider,
    displayName = displayName,
    modelId = modelId,
    isDefault = isDefault,
    sortOrder = sortOrder,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ProviderModel.toEntity() = ProviderModelEntity(
    id = id,
    provider = provider,
    displayName = displayName,
    modelId = modelId,
    isDefault = isDefault,
    sortOrder = sortOrder,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ConversationEntity.toDomain() = ChatConversation(
    id = id,
    title = title,
    provider = provider,
    modelId = modelId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ChatConversation.toEntity() = ConversationEntity(
    id = id,
    title = title,
    provider = provider,
    modelId = modelId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MessageEntity.toDomain() = ChatMessage(
    id = id,
    conversationId = conversationId,
    role = role,
    content = content,
    isStreaming = isStreaming,
    createdAt = createdAt,
    tokenCount = tokenCount
)

fun ChatMessage.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    role = role,
    content = content,
    isStreaming = isStreaming,
    createdAt = createdAt,
    tokenCount = tokenCount
)
