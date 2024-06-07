package dev.willfarris.llmchat.ui.chatview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.willfarris.llmchat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBox(messageSendHandler: (String) -> Unit) {
    var chatInputFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    val buttonColor = MaterialTheme.colorScheme.primary
    val buttonIcon = MaterialTheme.colorScheme.onPrimary

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = chatInputFieldValue,
            onValueChange = { newTextValue -> chatInputFieldValue = newTextValue },
            placeholder = {
                Text(text = "Send a message")
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
            ),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(24.dp),
            maxLines = 5,
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (chatInputFieldValue.text != "")
                    messageSendHandler(chatInputFieldValue.text)
                chatInputFieldValue = TextFieldValue("")
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(
                    CircleShape
                )
                .background(buttonColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_send_24),
                colorFilter = ColorFilter.tint(buttonIcon),
                contentDescription = "Send button",
            )
        }
    }
}

@Preview
@Composable
fun ChatInputBoxPreview() {
    ChatInputBox(messageSendHandler = {_ -> })
}