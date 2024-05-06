package dev.willfarris.llmchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.willfarris.llmchat.ui.settings.SettingsScreen
import dev.willfarris.llmchat.ui.settings.SettingsViewModel
import dev.willfarris.llmchat.ui.settings.SettingsViewModelFactory
import dev.willfarris.llmchat.ui.theme.AssistantTheme
import dev.willfarris.llmchat.ui.theme.ThemeManager

class SettingsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(application as ChatAssistantApplication) }
        val theme = ThemeManager.theme

        setContent {
            AssistantTheme(
                theme = theme.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(settingsViewModel)
                }
            }
        }
    }
}