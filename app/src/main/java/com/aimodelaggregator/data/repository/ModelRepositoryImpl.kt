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
}
