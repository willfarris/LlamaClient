package dev.willfarris.llmchat.ui.chatview

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.willfarris.llmchat.R
import dev.willfarris.llmchat.SettingsActivity

@Composable
fun AssistantTopBar(viewModel: ChatViewModel) {
    val onSurface = MaterialTheme.colorScheme.onSurface

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
    ) {
        ModelSelectDropdown(viewModel, onSurface)
        val context = LocalContext.current
        IconButton(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_settings_24),
                colorFilter = ColorFilter.tint(onSurface),
                contentDescription = "App settings",
            )
        }
    }

}