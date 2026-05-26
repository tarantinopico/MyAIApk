package com.aimodelaggregator.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aimodelaggregator.domain.models.ProviderModel
import com.aimodelaggregator.domain.models.ProviderType
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementScreen(
    viewModel: ModelManagementViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.models, key = { it.id }) { model ->
                ModelItemCard(
                    model = model,
                    onDelete = { viewModel.deleteModel(model) },
                    onSetDefault = { viewModel.setDefaultModel(model) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddModelDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { provider, name, modelId ->
                viewModel.addModel(
                    ProviderModel(
                        id = UUID.randomUUID().mostSignificantBits,
                        provider = provider,
                        displayName = name,
                        modelId = modelId,
                        isDefault = false
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ModelItemCard(
    model: ProviderModel,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(model.displayName, style = MaterialTheme.typography.titleMedium)
                    if (model.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Star, "Default", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    }
                }
                Text("Provider: ${model.provider.name}", style = MaterialTheme.typography.bodySmall)
                Text("ID: ${model.modelId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!model.isDefault) {
                TextButton(onClick = onSetDefault) {
                    Text("Set Default")
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddModelDialog(
    onDismiss: () -> Unit,
    onAdd: (ProviderType, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var modelId by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf(ProviderType.GROQ) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Model") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedProvider.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Provider") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ProviderType.entries.forEach { pt ->
                            DropdownMenuItem(
                                text = { Text(pt.name) },
                                onClick = {
                                    selectedProvider = pt
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = modelId,
                    onValueChange = { modelId = it },
                    label = { Text("Model ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(selectedProvider, name, modelId) },
                enabled = name.isNotBlank() && modelId.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
