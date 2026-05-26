package com.aimodelaggregator.domain.models

data class ProviderModel(
    val id: Long = 0,
    val provider: ProviderType,
    val displayName: String,
    val modelId: String,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
