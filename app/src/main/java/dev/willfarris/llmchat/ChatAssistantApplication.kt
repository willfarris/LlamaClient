package dev.willfarris.llmchat

import android.app.Application
import dev.willfarris.llmchat.data.ChatRepositoryImpl
import dev.willfarris.llmchat.data.OllamaApiImpl
import dev.willfarris.llmchat.data.api.ollama.OllamaAPIService
import dev.willfarris.llmchat.domain.ChatRepository
import dev.willfarris.llmchat.data.history.ChatHistoryDatabase
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.domain.OllamaApi
import dev.willfarris.llmchat.ui.theme.ThemeManager

class ChatAssistantApplication: Application() {
    val chatRepository: ChatRepository by lazy {
        ChatRepositoryImpl(
            ChatHistoryDatabase.getDatabase(this).chatHistoryDao()
        )
    }

    val ollamaApi: OllamaApi by lazy {
        OllamaApiImpl(
            OllamaAPIService.getInstance()
        )
    }

    override fun onCreate() {
        super.onCreate()
        ThemeManager.initialize(this)
        OllamaPreferencesManager.initialize(this)
    }
}