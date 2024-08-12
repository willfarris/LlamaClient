package dev.willfarris.llmchat.ui.health

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.willfarris.llmchat.ui.chatview.ChatViewModel

@Composable
fun HealthStatusCard(
    viewModel: ChatViewModel,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    val text  = if(viewModel.heartbeatState.value) "Ollama is connected" else "Error conencting to server"
                    Text(text)
                }
                if(viewModel.heartbeatState.value) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PsModelList(viewModel.activeModelsList)
                }
            }
        }
    }
}