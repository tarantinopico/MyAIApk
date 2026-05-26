package com.aimodelaggregator.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDrawerContent(
    conversationListViewModel: ConversationListViewModel,
    onConversationSelected: (Long) -> Unit,
    onNewChat: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateModels: () -> Unit
) {
    val uiState by conversationListViewModel.uiState.collectAsStateWithLifecycle()

    ModalDrawerSheet(
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Aggregator AI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onNewChat,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Chat")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Conversations", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.conversations.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.conversations, key = { it.id }) { conv ->
                        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(conv.updatedAt))
                        ListItem(
                            headlineContent = { Text(conv.title, maxLines = 1) },
                            supportingContent = { Text(date) },
                            modifier = Modifier.clickable { onConversationSelected(conv.id) }
                        )
                    }
                }
            } else {
                Text(
                    "No conversations yet",
                    modifier = Modifier.weight(1f).padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            ListItem(
                headlineContent = { Text("API Keys") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateSettings() }
            )

            ListItem(
                headlineContent = { Text("Models") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateModels() }
            )
        }
    }
}
