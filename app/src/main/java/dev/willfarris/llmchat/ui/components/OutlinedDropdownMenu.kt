package dev.willfarris.llmchat.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun OutlinedDropdownMenu(
    label: @Composable () -> Unit,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var currentModel by remember { mutableStateOf(selected) }
    var menuExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = currentModel,
        onValueChange = {},
        label = label
    )
    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
        options.forEach {
            DropdownMenuItem(text = { Text(it) }, onClick = { currentModel = it })
        }
    }
}
