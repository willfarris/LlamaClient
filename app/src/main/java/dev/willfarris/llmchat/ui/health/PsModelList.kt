package dev.willfarris.llmchat.ui.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.willfarris.llmchat.domain.Model

@Composable
fun PsModelList(activeModelList: List<Model>) {
    if(activeModelList.isEmpty()) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "No models loaded into memory",
                fontWeight = FontWeight.Bold,
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 2.dp)
        ) {
            Text(text = "Model name", fontWeight = FontWeight.Bold)
            Text(text = "VRAM used", fontWeight = FontWeight.Bold)
        }
        LazyColumn {
            items(
                activeModelList
            ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        val sizeMB = it.sizeVram!! / (1024 * 1024)
                        val sizeGB = sizeMB / 1024
                        val remainder = (sizeMB % 1024)
                        Text(it.name)
                        Text("${sizeGB}.$remainder GiB")
                    }

            }
        }
    }
}