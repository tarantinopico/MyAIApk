package com.aimodelaggregator.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aimodelaggregator.domain.models.ChatConversation
import com.aimodelaggregator.domain.models.ChatMessage
import com.aimodelaggregator.domain.models.ProviderModel
import com.aimodelaggregator.domain.models.ProviderType
import com.aimodelaggregator.domain.repository.ChatRepository
import com.aimodelaggregator.domain.repository.ConversationRepository
import com.aimodelaggregator.domain.repository.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val conversationId: Long? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val error: String? = null,
    val selectedProvider: ProviderType = ProviderType.GROQ,
    val selectedModel: ProviderModel? = null,
    val availableModels: List<ProviderModel> = emptyList()
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            modelRepository.getAllProviderModels().collect { models ->
                _uiState.update { it.copy(availableModels = models) }
                if (_uiState.value.selectedModel == null && models.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(selectedModel = models.firstOrNull { m -> m.isDefault } ?: models.first())
                    }
                }
            }
        }
    }

    fun selectModel(model: ProviderModel) {
        _uiState.update { it.copy(selectedModel = model, selectedProvider = model.provider) }
    }

    fun loadConversation(conversationId: Long) {
        viewModelScope.launch {
            val conv = conversationRepository.getConversationWithMessagesSync(conversationId)
            if (conv != null) {
                _uiState.update {
                    it.copy(
                        conversationId = conv.conversation.id,
                        messages = conv.messages
                    )
                }
            }
        }
    }
    
    fun createNewConversation() {
        _uiState.update { it.copy(conversationId = null, messages = emptyList(), error = null) }
    }

    fun sendMessage(content: String) {
        val currentState = _uiState.value
        val model = currentState.selectedModel
        if (model == null) {
            _uiState.update { it.copy(error = "No model selected") }
            return
        }

        viewModelScope.launch {
            try {
                var currentConvId = currentState.conversationId
                if (currentConvId == null) {
                    val newConv = ChatConversation(
                        title = content.take(30),
                        provider = currentState.selectedProvider,
                        modelId = model.modelId,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    currentConvId = conversationRepository.createConversation(newConv)
                    _uiState.update { it.copy(conversationId = currentConvId) }
                }
                
                val finalConvId = currentConvId!!

                _uiState.update { it.copy(isStreaming = true, streamingContent = "", error = null) }

                chatRepository.sendMessageStream(
                    conversationId = finalConvId,
                    message = content,
                    provider = currentState.selectedProvider,
                    modelId = model.modelId
                ).collect { chunk ->
                    _uiState.update { it.copy(streamingContent = chunk) }
                }

                _uiState.update { it.copy(isStreaming = false, streamingContent = "") }
                loadConversation(finalConvId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Unknown error", isStreaming = false) }
            }
        }
    }
}
