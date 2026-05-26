package com.aimodelaggregator.data.repository

import com.aimodelaggregator.data.database.dao.ProviderModelDao
import com.aimodelaggregator.data.mappers.toDomain
import com.aimodelaggregator.data.mappers.toEntity
import com.aimodelaggregator.domain.models.ProviderModel
import com.aimodelaggregator.domain.models.ProviderType
import com.aimodelaggregator.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ModelRepositoryImpl(
    private val modelDao: ProviderModelDao
) : ModelRepository {

    override fun getAllProviderModels(): Flow<List<ProviderModel>> {
        return modelDao.getAllModels().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getModelsForProvider(provider: ProviderType): Flow<List<ProviderModel>> {
        return modelDao.getModelsForProvider(provider).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveModel(model: ProviderModel): Long {
        return modelDao.insertModel(model.toEntity())
    }

    override suspend fun updateModel(model: ProviderModel) {
        modelDao.updateModel(model.toEntity())
    }

    override suspend fun deleteModel(model: ProviderModel) {
        modelDao.deleteModel(model.toEntity())
    }

    override suspend fun deleteModelsForProvider(provider: ProviderType) {
        modelDao.deleteModelsForProvider(provider)
    }

    override suspend fun seedDefaultModels() {
        if (modelDao.getModelCount() == 0) {
            val defaults = listOf(
                ProviderModel(provider = ProviderType.GROQ, modelId = "llama-3.1-8b-instant", displayName = "Llama 3.1 8B", isDefault = true),
                ProviderModel(provider = ProviderType.GROQ, modelId = "llama-3.1-70b-versatile", displayName = "Llama 3.1 70B", isDefault = false),
                ProviderModel(provider = ProviderType.GROQ, modelId = "llama-3.2-11b-vision-preview", displayName = "Llama 3.2 11B Vision", isDefault = false),
                ProviderModel(provider = ProviderType.CEREBRAS, modelId = "llama3.1-8b", displayName = "Llama 3.1 8B", isDefault = true),
                ProviderModel(provider = ProviderType.GEMINI, modelId = "gemini-2.5-flash", displayName = "Gemini 2.5 Flash", isDefault = true),
                ProviderModel(provider = ProviderType.GEMINI, modelId = "gemini-2.0-pro-exp-02-05", displayName = "Gemini 2.0 Pro Exp", isDefault = false)
            )
            defaults.forEach { saveModel(it) }
        }
    }
}
