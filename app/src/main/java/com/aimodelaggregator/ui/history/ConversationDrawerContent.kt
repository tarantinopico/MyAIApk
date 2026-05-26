package com.aimodelaggregator.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    var deleteCandidateId by remember { mutableStateOf<Long?>(null) }

    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        windowInsets = WindowInsets.safeDrawing
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            Text(
                "Aggregator AI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Button(
                    onClick = onNewChat,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Chat", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Recent", 
                style = MaterialTheme.typography.labelMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.conversations.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(uiState.conversations, key = { it.id }) { conv ->
                        val date = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(conv.updatedAt))
                        
                        ListItem(
                            headlineContent = { 
                                Text(
                                    conv.title.ifBlank { "New Conversation" },
                                    maxLines = 1, 
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Medium
                                ) 
                            },
                            supportingContent = { 
                                Text("\${conv.provider.name} • \$date", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            },
                            leadingContent = {
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                            },
                            trailingContent = {
                                IconButton(onClick = { deleteCandidateId = conv.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onConversationSelected(conv.id) }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        "No conversations yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                ListItem(
                    headlineContent = { Text("API Keys", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.Key, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateSettings() }
                )

                ListItem(
                    headlineContent = { Text("Models", fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateModels() }
                )
            }
        }
    }

    if (deleteCandidateId != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidateId = null },
            title = { Text("Delete Conversation") },
            text = { Text("Are you sure you want to delete this conversation? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteCandidateId?.let { conversationListViewModel.deleteConversation(it) }
                        deleteCandidateId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidateId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
