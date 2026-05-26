package com.aimodelaggregator.data.database.dao

import androidx.room.*
import com.aimodelaggregator.data.database.entity.ProviderModelEntity
import com.aimodelaggregator.domain.models.ProviderType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderModelDao {

    @Query("SELECT * FROM provider_models ORDER BY provider ASC, sortOrder ASC")
    fun getAllModels(): Flow<List<ProviderModelEntity>>

    @Query("SELECT * FROM provider_models WHERE provider = :provider ORDER BY sortOrder ASC")
    fun getModelsForProvider(provider: ProviderType): Flow<List<ProviderModelEntity>>

    @Query("SELECT COUNT(*) FROM provider_models")
    suspend fun getModelCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ProviderModelEntity): Long

    @Update
    suspend fun updateModel(model: ProviderModelEntity)

    @Delete
    suspend fun deleteModel(model: ProviderModelEntity)

    @Query("DELETE FROM provider_models WHERE provider = :provider")
    suspend fun deleteModelsForProvider(provider: ProviderType)
}
