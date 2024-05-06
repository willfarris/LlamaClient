package dev.willfarris.llmchat.ui.chatview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.willfarris.llmchat.ChatAssistantApplication


class ChatViewModelFactory(private val application: ChatAssistantApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(application) as T
    }
}