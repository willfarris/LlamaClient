package dev.willfarris.llmchat.ui.chatview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.willfarris.llmchat.R
import dev.willfarris.llmchat.data.api.ollama.model.ModelInfo

@Composable
fun ModelSelectDropdown(
    availableModelsList: List<ModelInfo>,
    curModel: String?,
    onModelSelectedCallback: (Int) -> Unit,
    textColor: Color,
    fontSize: TextUnit = 18.sp,
) {
    var modelDropdownExpanded by remember { mutableStateOf(false) }
    val curModelText = if(curModel == null && availableModelsList.isNotEmpty()) {
        "Select a model"
    } else curModel ?: "No models available"

    Row(
        modifier = Modifier
            .clickable { modelDropdownExpanded = !modelDropdownExpanded }
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = curModelText,
            fontSize = fontSize,
            color = textColor,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(start = 4.dp),
        )
        DropdownMenu(
            expanded = modelDropdownExpanded,
            onDismissRequest = {modelDropdownExpanded = !modelDropdownExpanded},
        ) {
            availableModelsList.forEachIndexed { index, modelInfo ->
                DropdownMenuItem(
                    text = { Text(modelInfo.name) },
                    onClick =  {
                        onModelSelectedCallback(index)
                        modelDropdownExpanded = !modelDropdownExpanded
                    }
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.baseline_arrow_drop_down_32),
            contentDescription = "Pick model dropdown",
            colorFilter = ColorFilter.tint(textColor),
        )
    }
}