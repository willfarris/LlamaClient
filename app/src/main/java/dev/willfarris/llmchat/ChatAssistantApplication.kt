package dev.willfarris.llmchat

import android.app.Application
import dev.willfarris.llmchat.data.ChatRepository
import dev.willfarris.llmchat.data.chathistory.ChatHistoryDatabase
import dev.willfarris.llmchat.data.ollama.OllamaAPIService
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.ui.theme.ThemeManager


class ChatAssistantApplication: Application() {
    val chatAssistantRepository: ChatRepository by lazy {
        ChatRepository(
            ChatHistoryDatabase.getDatabase(this).chatHistoryDao(),
            OllamaAPIService.getInstance()
        )
    }

    override fun onCreate() {
        super.onCreate()
        ThemeManager.initialize(this)
        OllamaPreferencesManager.initialize(this)
    }
}