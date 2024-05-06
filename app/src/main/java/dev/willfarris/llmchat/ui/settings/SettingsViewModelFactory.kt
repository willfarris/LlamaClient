package dev.willfarris.llmchat.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.willfarris.llmchat.ChatAssistantApplication


class SettingsViewModelFactory(private val application: ChatAssistantApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(application) as T
    }
}