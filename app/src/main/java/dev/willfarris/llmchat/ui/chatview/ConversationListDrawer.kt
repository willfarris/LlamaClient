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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.willfarris.llmchat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListDrawer(viewModel: ChatViewModel) {

    //val drawerColor = MaterialTheme.colorScheme.surfaceContainer
    val bubbleColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val bubbleTextColor = MaterialTheme.colorScheme.onSurface
    val selectedBubbleColor = MaterialTheme.colorScheme.primaryContainer
    val selectedBubbleTextColor = MaterialTheme.colorScheme.onPrimaryContainer

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
            items(
                viewModel.chatsList.asReversed(),
                key = { chat -> chat.chatId }
            ) { chat ->
                val backgroundColor =
                    if (chat.chatId == viewModel.curChatId) selectedBubbleColor else bubbleColor
                val textColor =
                    if (chat.chatId == viewModel.curChatId) selectedBubbleTextColor else bubbleTextColor

                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .clickable { viewModel.selectChat(chat.chatId) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var editState = remember { mutableStateOf(false) }
                    val focusRequester = remember { FocusRequester() }
                    var editName = remember { chat.chatName }
                    var chatDropdownExpanded by remember { mutableStateOf(false) }

                    if (!editState.value) {
                        Text(
                            text = chat.chatName.value,
                            color = textColor,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                        )
                    } else {
                        TextField(
                            value = editName.value,
                            onValueChange = { newTextValue -> editName.value = newTextValue },
                            placeholder = {
                                Text(text = "Chat Name")
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Go,
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    viewModel.updateChatName(chat.chatId, editName.value)
                                    editState.value = false
                                }
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = backgroundColor,
                                focusedPlaceholderColor = textColor,
                                focusedTextColor = textColor,
                                cursorColor = textColor,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            visualTransformation = VisualTransformation
                                .None,
                            maxLines = 1,
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onGloballyPositioned { focusRequester.requestFocus() },
                        )
                    }

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
                                    text = "Edit name",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            editState.value = true
                                            chatDropdownExpanded = !chatDropdownExpanded
                                        },
                                )
                                Text(
                                    AnnotatedString("Delete"),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.deleteChat(chat.chatId)
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
}