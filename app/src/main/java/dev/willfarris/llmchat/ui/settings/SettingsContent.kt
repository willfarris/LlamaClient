package dev.willfarris.llmchat.ui.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.alorma.compose.settings.ui.SettingsGroup
import dev.willfarris.llmchat.ui.settings.components.SettingsDropdown
import dev.willfarris.llmchat.ui.settings.components.SettingsTextDialog

@Composable
fun SettingsContent(viewModel: SettingsViewModel) {
    SettingsGroup(
        title = { Text("Appearance") }
    ) {
        SettingsDropdown(
            title = "Theme",
            subtitle = "Choose between light, dark and system colors",
            selected = viewModel.selectedTheme.value.name.lowercase().replaceFirstChar { c -> c.uppercaseChar() },
            options = viewModel.themeOptions.map { s -> s.lowercase().replaceFirstChar { c -> c.uppercaseChar() } },
        ) {selectedTheme -> viewModel.setTheme(selectedTheme.uppercase())}
    }
    SettingsGroup(
        title = { Text("Ollama settings") }
    ) {
        for(preferenceOption in viewModel.ollamaSettings) {
            SettingsTextDialog(
                title = preferenceOption.title,
                subtitle = preferenceOption.subtitle,
                hint = preferenceOption.defaultValue,
                getCurrentSetting = preferenceOption.getCurrentSetting,
                onConfirmSetting = preferenceOption.onChanged,
            )
        }
    }
}