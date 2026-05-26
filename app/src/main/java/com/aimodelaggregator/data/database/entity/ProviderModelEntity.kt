package com.aimodelaggregator.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aimodelaggregator.domain.models.ProviderType

@Entity(
    tableName = "provider_models",
    indices = [
        Index(value = ["provider", "modelId"], unique = true)
    ]
)
data class ProviderModelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val provider: ProviderType,
    val displayName: String,
    val modelId: String,
    val isDefault: Boolean,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long
)
