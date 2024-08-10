package dev.willfarris.llmchat.ui.chatview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.willfarris.llmchat.R
import dev.willfarris.llmchat.ui.components.DialogPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListDrawer(viewModel: ChatViewModel) {

    val bubbleColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val bubbleTextColor = MaterialTheme.colorScheme.onSurface
    val selectedBubbleColor = MaterialTheme.colorScheme.primaryContainer
    val selectedBubbleTextColor = MaterialTheme.colorScheme.onPrimaryContainer

    var showEditDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf(-1) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(
                viewModel.chatsList,
                key = { _, chat -> chat.chatId }
            ) { index, chat ->
                val backgroundColor =
                    if (index == viewModel.curChatIndex) selectedBubbleColor else bubbleColor
                val textColor =
                    if (index == viewModel.curChatIndex) selectedBubbleTextColor else bubbleTextColor

                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .clickable { viewModel.selectChat(index) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var chatDropdownExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = chat.chatTitle.value,
                        color = textColor,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                    )

                    IconButton(
                        onClick = { chatDropdownExpanded = !chatDropdownExpanded }
                    ) {
                        Image(
                            painterResource(id = R.drawable.baseline_more_vert_24),
                            contentDescription = "Edit chat",
                            colorFilter = ColorFilter.tint(textColor),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        DropdownMenu(
                            expanded = chatDropdownExpanded,
                            onDismissRequest = {
                                chatDropdownExpanded = !chatDropdownExpanded
                            }) {
                            Column {
                                Text(
                                    text = "Edit",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            editIndex = index
                                            chatDropdownExpanded = !chatDropdownExpanded
                                            showEditDialog = true
                                        },
                                )
                                Text(
                                    AnnotatedString("Delete"),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.deleteChat(index)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.createNewChat() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.baseline_add_48),
                    contentDescription = "New chat",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }
    }

    if(showEditDialog) {
        val editChat = viewModel.chatsList[editIndex]
        val chatTitle: MutableState<String> = mutableStateOf(editChat.chatTitle.value)
        val chatPrompt: MutableState<String> = mutableStateOf(editChat.chatPrompt.value)
        val chatContextSize: MutableState<String> = mutableStateOf(editChat.chatContextSize.value)
        val preferredModel: MutableState<String?> = mutableStateOf(editChat.chatModel)

        var expanded by remember { mutableStateOf(false) }

        DialogPopup(
            title = {
                Text(
                    text = "Edit chat",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            },
            content = {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedTextField(
                        label = { Text("Chat Title") },
                        value = chatTitle.value,
                        onValueChange = { newTitle -> chatTitle.value = newTitle },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = preferredModel.value ?: "Select a model",
                            label = { Text("Model") },
                            onValueChange = {},
                            modifier = Modifier
                                .menuAnchor()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            viewModel.modelsList.forEach {model ->
                                DropdownMenuItem(
                                    text = { Text(model.name) },
                                    onClick = {
                                        preferredModel.value = model.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        label = { Text("Context size") },
                        placeholder = { Text("Override the default context size for this chat") },
                        value = chatContextSize.value,
                        onValueChange = { newText -> chatContextSize.value = newText },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                    OutlinedTextField(
                        label = { Text("System Prompt") },
                        placeholder = { Text("Override the default system prompt for this chat") },
                        value = chatPrompt.value,
                        onValueChange = { newText -> chatPrompt.value = newText },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        maxLines = 8,
                    )
                }
            },
            onConfirm = {
                viewModel.updateChatSettings(
                    editIndex,
                    chatTitle = chatTitle.value,
                    chatModel = preferredModel.value,
                    contextSize = chatContextSize.value,
                    chatPrompt = chatPrompt.value,
                )
                showEditDialog = false
            },
            onDismissRequest = {showEditDialog = false})
    }
}