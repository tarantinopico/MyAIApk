package com.aimodelaggregator.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aimodelaggregator.ui.chat.ChatScreen
import com.aimodelaggregator.ui.chat.ChatViewModel
import com.aimodelaggregator.ui.history.ConversationDrawerContent
import com.aimodelaggregator.ui.history.ConversationListViewModel
import com.aimodelaggregator.ui.settings.ModelManagementScreen
import com.aimodelaggregator.ui.settings.ModelManagementViewModel
import com.aimodelaggregator.ui.settings.SettingsScreen
import com.aimodelaggregator.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val chatViewModel: ChatViewModel = viewModel(factory = ViewModelFactory.Factory)
    val conversationListViewModel: ConversationListViewModel = viewModel(factory = ViewModelFactory.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = ViewModelFactory.Factory)
    val modelManagementViewModel: ModelManagementViewModel = viewModel(factory = ViewModelFactory.Factory)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ConversationDrawerContent(
                conversationListViewModel = conversationListViewModel,
                onConversationSelected = { convId ->
                    chatViewModel.loadConversation(convId)
                    scope.launch { drawerState.close() }
                    navController.navigate("chat") {
                        popUpTo("chat") { inclusive = true }
                    }
                },
                onNewChat = {
                    chatViewModel.createNewConversation()
                    scope.launch { drawerState.close() }
                    navController.navigate("chat") {
                        popUpTo("chat") { inclusive = true }
                    }
                },
                onNavigateSettings = {
                    scope.launch { drawerState.close() }
                    navController.navigate("settings")
                },
                onNavigateModels = {
                    scope.launch { drawerState.close() }
                    navController.navigate("models")
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("chat") {
                ChatScreen(
                    viewModel = chatViewModel,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("models") {
                ModelManagementScreen(
                    viewModel = modelManagementViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
