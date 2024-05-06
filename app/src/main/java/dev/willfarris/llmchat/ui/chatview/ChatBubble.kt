package dev.willfarris.llmchat.ui.chatview

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ChatBubble(
    message: ChatMessageUiContent,
) {
    val isAssistant = message.role == "assistant"
    val bubbleColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val bubbleContentColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            /*Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*Image(
                    painterResource(id = iconId),
                    contentDescription = "Account profile",
                    colorFilter = ColorFilter.tint(bubbleInfoColor),
                    modifier = Modifier.padding(end = 4.dp)
                )*/

            }*/
            Text(
                text = if(isAssistant) message.modelName else message.role,
                fontStyle = androidx.compose.ui.text.font.FontStyle(FontStyle.FONT_WEIGHT_BOLD),
                modifier = Modifier.padding(start=8.dp)
            )
            Card(
                modifier = Modifier.padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = bubbleColor,
                    contentColor = bubbleContentColor
                )
            ) {
                MarkdownText(
                    markdown = message.content.value,
                    isTextSelectable = true,
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        color = bubbleContentColor
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
fun ChatBubblePreview() {
    val userMessage = ChatMessageUiContent("user", mutableStateOf("Hello!"), "llama3:latest")
    val assistantMessage = ChatMessageUiContent("assistant", mutableStateOf("Hi there! How can I help you?"), "llama3:latest")
    Column {
        ChatBubble(message = userMessage)
        ChatBubble(message = assistantMessage)
    }
}