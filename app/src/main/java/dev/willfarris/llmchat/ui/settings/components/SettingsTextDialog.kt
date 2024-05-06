package dev.willfarris.llmchat.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alorma.compose.settings.ui.base.internal.SettingsTileScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTextDialog(
    title: String,
    subtitle: String,
    hint: String,
    getCurrentSetting: () -> String,
    onConfirmSetting: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    if(showDialog) {
        TextDialogPopup(
            title = title,
            hint = hint,
            getCurrentSetting = getCurrentSetting,
            onConfirmRequest = onConfirmSetting,
            onDismissRequest = { if (showDialog) showDialog = false }
        )
    }

    SettingsTileScaffold(
        title = { Text(title) },
        subtitle = { Text(subtitle) },
        modifier = Modifier.clickable { if(!showDialog) showDialog = true }
    ) {
        Text(
            text = getCurrentSetting(),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}