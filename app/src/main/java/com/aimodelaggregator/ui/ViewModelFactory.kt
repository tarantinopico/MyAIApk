package com.aimodelaggregator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.aimodelaggregator.AIModelAggregatorApplication
import com.aimodelaggregator.ui.chat.ChatViewModel
import com.aimodelaggregator.ui.history.ConversationListViewModel
import com.aimodelaggregator.ui.settings.ModelManagementViewModel
import com.aimodelaggregator.ui.settings.SettingsViewModel

object ViewModelFactory {
    val Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AIModelAggregatorApplication
            val container = application.container
            return when {
                modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                    ChatViewModel(
                        chatRepository = container.chatRepository,
                        modelRepository = container.modelRepository,
                        conversationRepository = container.conversationRepository
                    ) as T
                }
                modelClass.isAssignableFrom(ConversationListViewModel::class.java) -> {
                    ConversationListViewModel(
                        conversationRepository = container.conversationRepository
                    ) as T
                }
                modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                    SettingsViewModel(
                        providerSettingsRepository = container.providerSettingsRepository,
                        conversationRepository = container.conversationRepository
                    ) as T
                }
                modelClass.isAssignableFrom(ModelManagementViewModel::class.java) -> {
                    ModelManagementViewModel(
                        modelRepository = container.modelRepository
                    ) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
