package dev.willfarris.llmchat.ui.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.willfarris.llmchat.ui.components.DialogPopup

@Composable
fun TextDialogPopup(
    title: String,
    hint: String,
    getCurrentSetting: () -> String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(getCurrentSetting())) }
    DialogPopup(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
            )
        },
        content = {
            OutlinedTextField(
                textFieldValue,
                onValueChange = { str -> textFieldValue =  str },
                placeholder = { Text(text = hint, fontSize = 12.sp) },
                shape = RoundedCornerShape(8.dp),
                maxLines = 5,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .wrapContentSize()
            )
        },
        onConfirm = { onConfirmRequest(textFieldValue.text) },
        onDismissRequest = {onDismissRequest()}
    )
}