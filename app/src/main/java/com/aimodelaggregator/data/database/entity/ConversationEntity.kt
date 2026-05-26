package com.aimodelaggregator.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aimodelaggregator.domain.models.ProviderType

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val provider: ProviderType,
    val modelId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val draftMessage: String? = null
)
