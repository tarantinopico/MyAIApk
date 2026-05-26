package com.aimodelaggregator.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aimodelaggregator.domain.models.ChatConversation
import com.aimodelaggregator.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConversationListUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val isLoading: Boolean = true
)

class ConversationListViewModel(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            conversationRepository.getAllConversations().collect { list ->
                _uiState.update { it.copy(conversations = list, isLoading = false) }
            }
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            conversationRepository.deleteConversation(id)
        }
    }
}
