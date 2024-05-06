package dev.willfarris.llmchat.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun TextDialogPopup(
    title: String,
    hint: String,
    getCurrentSetting: () -> String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest ) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                )

                var textFieldValue by remember { mutableStateOf(TextFieldValue(getCurrentSetting())) }
                OutlinedTextField(
                    textFieldValue,
                    onValueChange = { str -> textFieldValue =  str },
                    placeholder = { Text(text = hint, fontSize = 12.sp) },
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                        .wrapContentSize()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Dismiss",
                        modifier = Modifier
                            .clickable { onDismissRequest() }
                            .padding(vertical = 16.dp)
                    )
                    Text(
                        text = "Save",
                        modifier = Modifier
                            .clickable {
                                onConfirmRequest(textFieldValue.text)
                                onDismissRequest()
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}