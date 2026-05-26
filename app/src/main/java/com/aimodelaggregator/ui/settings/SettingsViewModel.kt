package com.aimodelaggregator.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aimodelaggregator.domain.models.ProviderType
import com.aimodelaggregator.domain.repository.ProviderSettingsRepository
import com.aimodelaggregator.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val groqKeyExists: Boolean = false,
    val cerebrasKeyExists: Boolean = false,
    val geminiKeyExists: Boolean = false
)

class SettingsViewModel(
    private val providerSettingsRepository: ProviderSettingsRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkKeys()
    }

    private fun checkKeys() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    groqKeyExists = !providerSettingsRepository.getApiKey(ProviderType.GROQ).isNullOrBlank(),
                    cerebrasKeyExists = !providerSettingsRepository.getApiKey(ProviderType.CEREBRAS).isNullOrBlank(),
                    geminiKeyExists = !providerSettingsRepository.getApiKey(ProviderType.GEMINI).isNullOrBlank()
                )
            }
        }
    }

    fun saveApiKey(provider: ProviderType, key: String) {
        viewModelScope.launch {
            providerSettingsRepository.saveApiKey(provider, key)
            checkKeys()
        }
    }

    fun clearApiKey(provider: ProviderType) {
        viewModelScope.launch {
            providerSettingsRepository.saveApiKey(provider, "")
            checkKeys()
        }
    }

    fun resetLocalData() {
        viewModelScope.launch {
            conversationRepository.deleteAllConversations()
        }
    }
}
