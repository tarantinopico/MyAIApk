package com.aimodelaggregator.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aimodelaggregator.domain.models.ProviderModel
import com.aimodelaggregator.domain.repository.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModelManagementUiState(
    val models: List<ProviderModel> = emptyList()
)

class ModelManagementViewModel(
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelManagementUiState())
    val uiState: StateFlow<ModelManagementUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            modelRepository.getAllProviderModels().collect { list ->
                _uiState.update { it.copy(models = list) }
            }
        }
    }

    fun addModel(model: ProviderModel) {
        viewModelScope.launch {
            modelRepository.saveModel(model)
        }
    }

    fun deleteModel(model: ProviderModel) {
        viewModelScope.launch {
            modelRepository.deleteModel(model)
        }
    }

    fun setDefaultModel(model: ProviderModel) {
        viewModelScope.launch {
            val all = _uiState.value.models
            all.forEach { 
                modelRepository.saveModel(it.copy(isDefault = false))
            }
            modelRepository.saveModel(model.copy(isDefault = true))
        }
    }
}
