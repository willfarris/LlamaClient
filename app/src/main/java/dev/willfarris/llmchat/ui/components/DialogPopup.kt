package dev.willfarris.llmchat.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DialogPopup(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest ) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                title()
                content()
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
                        text = "Confirm",
                        modifier = Modifier
                            .clickable {
                                onConfirm()
                                onDismissRequest()
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}