package dev.willfarris.llmchat

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.willfarris.llmchat.data.ChatRepository
import dev.willfarris.llmchat.data.history.ChatHistoryDatabase
import dev.willfarris.llmchat.data.api.ollama.OllamaAPIService
import dev.willfarris.llmchat.data.api.ollama.health.HealthStatus
import dev.willfarris.llmchat.data.preferences.OllamaPreferencesManager
import dev.willfarris.llmchat.ui.chatview.tickerFlow
import dev.willfarris.llmchat.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.time.Duration


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