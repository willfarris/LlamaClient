package dev.willfarris.llmchat.ui.chatview

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: ChatViewModel.MessageUiContent,
    onDelete: () -> Unit,
    onRegenerate: () -> Unit,
) {
    val isAssistant = message.role == "assistant"
    val haptics = LocalHapticFeedback.current
    var popup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    popup = !popup
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ),
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
        ) {
            Text(
                text = if (isAssistant) message.modelName else message.role,
                fontStyle = androidx.compose.ui.text.font.FontStyle(FontStyle.FONT_WEIGHT_BOLD),
                modifier = Modifier.padding(start = 8.dp)
            )
            ChatBubbleCard(
                message = message,
                textSelectable = false,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if(popup) {
        Dialog(onDismissRequest = { popup = false }) {
            Column {
                ChatBubbleCard(
                    message = message,
                    textSelectable = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(onClick = {onRegenerate(); popup = false}) {
                        Text("Regenerate")
                    }
                    Button(onClick = {onDelete(); popup = false}) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleCard(
    message: ChatViewModel.MessageUiContent,
    textSelectable: Boolean,
    modifier: Modifier = Modifier,
) {
    val bubbleColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val bubbleContentColor = MaterialTheme.colorScheme.onSurface
    Card(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = bubbleColor,
            contentColor = bubbleContentColor
        ),
    ) {
        MarkdownText(
            markdown = message.content.value,
            isTextSelectable = textSelectable,
            disableLinkMovementMethod = !textSelectable,
            modifier = Modifier
                .padding(16.dp),
            style = TextStyle(
                color = bubbleContentColor
            )
        )
    }
}