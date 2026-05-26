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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

data class ChatUiState(
    val conversationId: Long? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isStreaming: Boolean = false,
    val streamingContent: String = "",
    val error: String? = null,
    val selectedProvider: ProviderType = ProviderType.GROQ,
    val selectedModel: ProviderModel? = null,
    val availableModels: List<ProviderModel> = emptyList(),
    val draftMessage: String = ""
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var streamingJob: Job? = null

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
        stopStreaming()
        viewModelScope.launch {
            val conv = conversationRepository.getConversationWithMessagesSync(conversationId)
            if (conv != null) {
                _uiState.update {
                    it.copy(
                        conversationId = conv.conversation.id,
                        messages = conv.messages,
                        error = null,
                        draftMessage = conv.conversation.draftMessage ?: ""
                    )
                }
            }
        }
    }
    
    fun createNewConversation() {
        stopStreaming()
        _uiState.update { it.copy(conversationId = null, messages = emptyList(), error = null, streamingContent = "", draftMessage = "") }
    }

    fun stopStreaming() {
        streamingJob?.cancel()
        streamingJob = null
        if (_uiState.value.isStreaming) {
            _uiState.update { it.copy(isStreaming = false, streamingContent = "") }
            _uiState.value.conversationId?.let { loadConversation(it) }
        }
    }

    fun retryLastMessage() {
        val currentState = _uiState.value
        if (currentState.isStreaming) return
        
        val lastUserIndex = currentState.messages.indexOfLast { it.role == com.aimodelaggregator.domain.models.MessageRole.USER }
        if (lastUserIndex == -1) return
        
        val lastUserMessage = currentState.messages[lastUserIndex]
        
        // Remove or ignore following assistant messages in UI for a clean look, 
        // but technically if we just send again it will append.
        sendMessage(lastUserMessage.content)
    }

    private var draftSaveJob: Job? = null

    fun updateDraft(content: String) {
        _uiState.update { it.copy(draftMessage = content) }
        val convId = _uiState.value.conversationId
        if (convId != null) {
            draftSaveJob?.cancel()
            draftSaveJob = viewModelScope.launch {
                delay(500)
                val conv = conversationRepository.getConversationWithMessagesSync(convId)
                if (conv != null) {
                    val updated = conv.conversation.copy(draftMessage = content)
                    conversationRepository.updateConversation(updated)
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val currentState = _uiState.value
        val model = currentState.selectedModel
        if (model == null) {
            _uiState.update { it.copy(error = "No model selected") }
            return
        }

        stopStreaming()

        streamingJob = viewModelScope.launch {
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

                _uiState.update { it.copy(isStreaming = true, streamingContent = "", error = null, draftMessage = "") }

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
            } catch (e: CancellationException) {
                // Handled gracefully in stopStreaming or by job cancellation
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Unknown error", isStreaming = false) }
            } finally {
                streamingJob = null
            }
        }
    }
}
