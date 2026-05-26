package com.aimodelaggregator.domain.repository

import com.aimodelaggregator.domain.models.ProviderModel
import com.aimodelaggregator.domain.models.ProviderType
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getAllProviderModels(): Flow<List<ProviderModel>>
    fun getModelsForProvider(provider: ProviderType): Flow<List<ProviderModel>>
    suspend fun saveModel(model: ProviderModel): Long
    suspend fun updateModel(model: ProviderModel)
    suspend fun deleteModel(model: ProviderModel)
    suspend fun deleteModelsForProvider(provider: ProviderType)
    suspend fun seedDefaultModels()
}
