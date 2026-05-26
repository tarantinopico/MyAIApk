package com.aimodelaggregator.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aimodelaggregator.domain.models.ProviderType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Keys") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProviderApiKeySection(
                providerName = "Groq",
                providerType = ProviderType.GROQ,
                isConfigured = uiState.groqKeyExists,
                onSave = { viewModel.saveApiKey(ProviderType.GROQ, it) },
                onClear = { viewModel.clearApiKey(ProviderType.GROQ) }
            )

            ProviderApiKeySection(
                providerName = "Cerebras",
                providerType = ProviderType.CEREBRAS,
                isConfigured = uiState.cerebrasKeyExists,
                onSave = { viewModel.saveApiKey(ProviderType.CEREBRAS, it) },
                onClear = { viewModel.clearApiKey(ProviderType.CEREBRAS) }
            )
        }
    }
}

@Composable
fun ProviderApiKeySection(
    providerName: String,
    providerType: ProviderType,
    isConfigured: Boolean,
    onSave: (String) -> Unit,
    onClear: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(providerName, style = MaterialTheme.typography.titleMedium)
                if (isConfigured) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, "Configured", tint = Color(0xFF4CAF50))
                        Spacer(Modifier.width(4.dp))
                        Text("Configured", color = Color(0xFF4CAF50))
                    }
                } else {
                    Text("Not Configured", color = MaterialTheme.colorScheme.error)
                }
            }

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (isConfigured) {
                    TextButton(onClick = {
                        apiKey = ""
                        onClear()
                    }) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onSave(apiKey) },
                    enabled = apiKey.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}
