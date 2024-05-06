package dev.willfarris.llmchat.ui.chatview

import android.R.attr.value
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import dev.willfarris.llmchat.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val messageListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(viewModel.messagesList.size) {
        messageListState.animateScrollToItem(0)
    }

    val menuRotation = remember { mutableStateOf(0.0f) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ConversationListDrawer(viewModel)
            }
        }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if(drawerState.isOpen) {
                                    drawerState.close()
                                } else if(drawerState.isClosed) {
                                    drawerState.open()
                                }
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_menu_24),
                                contentDescription = "Menu",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier.rotate(menuRotation.value)
                            )
                        }
                    },
                    title = { AssistantTopBar(viewModel) },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        //scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                )
            },
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f),
                    reverseLayout = true,
                    state = messageListState,
                ) {
                    items(
                        viewModel.messagesList.asReversed(),
                        key = { message -> message.id }
                    ) { message ->
                        ChatBubble(message)
                    }
                }
                ChatInputBox(
                    messageSendHandler = { msg ->
                        viewModel.sendMessageFromUser(msg)
                        coroutineScope.launch {
                            messageListState.animateScrollToItem(0, 0)
                        }
                    },
                )
            }
        }
    }
}