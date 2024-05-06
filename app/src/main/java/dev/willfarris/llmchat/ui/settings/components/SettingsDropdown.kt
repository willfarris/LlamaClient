package dev.willfarris.llmchat.ui.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.base.internal.SettingsTileScaffold
import dev.willfarris.llmchat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdown(
    title: String,
    subtitle: String,
    selected: String,
    options: List<String>,
    selectedCallback: (String) -> Unit,
) {
    var showDropdown by remember { mutableStateOf(false) }

    SettingsTileScaffold(
        title = { Text(title) },
        subtitle = { Text(subtitle) },
    ) {
        Row(
            modifier = Modifier.clickable { showDropdown = !showDropdown },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected,
                modifier = Modifier.padding(4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Image(
                painter = painterResource(R.drawable.baseline_arrow_drop_down_32),
                contentDescription = "Pick model dropdown",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            if(showDropdown) {
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = !showDropdown }
                ) {
                    options.forEach {it ->
                        DropdownMenuItem(text = { Text(it) }, onClick = { selectedCallback(it); showDropdown = !showDropdown })
                    }
                }
            }
        }
    }


}