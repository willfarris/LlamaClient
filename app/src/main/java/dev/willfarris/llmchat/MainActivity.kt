package dev.willfarris.llmchat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.willfarris.llmchat.ui.theme.AssistantTheme
import dev.willfarris.llmchat.ui.chatview.ChatScreen
import dev.willfarris.llmchat.ui.chatview.ChatViewModel
import dev.willfarris.llmchat.ui.chatview.ChatViewModelFactory
import dev.willfarris.llmchat.ui.theme.ThemeManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatViewModel: ChatViewModel by viewModels { ChatViewModelFactory(application as ChatAssistantApplication) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.errorMessage.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
                        chatViewModel.errorMessageComplete()
                    }
                }
            }
        }

        val theme = ThemeManager.theme

        setContent {
            AssistantTheme(
                theme = theme.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(chatViewModel)
                }
            }
        }
    }
}
