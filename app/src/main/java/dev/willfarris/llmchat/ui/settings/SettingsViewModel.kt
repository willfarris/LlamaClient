package dev.willfarris.llmchat.ui.settings

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dev.willfarris.llmchat.ChatAssistantApplication
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.ui.theme.ThemeManager

data class OllamaPreferenceOption(
    val title: String,
    val subtitle: String,
    val defaultValue: String,
    val getCurrentSetting: () -> String,
    val onChanged: (String) -> Unit,
)

class SettingsViewModel(application: ChatAssistantApplication): ViewModel() {
    var selectedTheme = ThemeManager.theme
    val themeOptions: List<String> = ThemeManager.themeOptions()

    val ollamaSettings: Array<OllamaPreferenceOption> = arrayOf(
        OllamaPreferenceOption(
            title = "Ollama endpoint",
            subtitle = "Remote URL of the Ollama API",
            defaultValue = OllamaPreferencesManager.DEFAULT_ENDPOINT,
            getCurrentSetting = { OllamaPreferencesManager.endpointUrl },
            onChanged = { newEndpointUrl -> OllamaPreferencesManager.endpointUrl = newEndpointUrl},
        ),
        OllamaPreferenceOption(
            title = "Default context size",
            subtitle = "Set the context size for new chats",
            defaultValue = OllamaPreferencesManager.DEFAULT_CONTEXT_SIZE.toString(),
            getCurrentSetting = { OllamaPreferencesManager.contextSize.toString() },
            onChanged = { newContextSize ->
                val newContextSizeInt: Int? = newContextSize.toIntOrNull()
                if (newContextSizeInt != null) {
                    OllamaPreferencesManager.contextSize = newContextSizeInt
                } else {
                    Toast.makeText(
                        application,
                        "$newContextSize is not a valid context size, keeping previous",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ),
        OllamaPreferenceOption(
            title = "Default system prompt",
            subtitle = "Set the system prompt for new chats",
            defaultValue = OllamaPreferencesManager.DEFAULT_SYSTEM_PROMPT,
            getCurrentSetting =  { OllamaPreferencesManager.systemPrompt },
            onChanged = { newSystemPrompt -> OllamaPreferencesManager.systemPrompt = newSystemPrompt },
        )
    )

    fun setTheme(newTheme: String) {
        ThemeManager.setThemeFromString(newTheme)
    }
}