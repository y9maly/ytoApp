package me.maly.y9to.compose.components.writePost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun rememberWritePostState(
    text: TextFieldValue = TextFieldValue(),
    expanded: Boolean = false,
) = rememberSaveable(saver = WritePostState.Saver) {
    WritePostState(
        text = text,
        expanded = expanded,
    )
}

class WritePostState(
    expanded: Boolean,
    text: TextFieldValue,
) {
    companion object {
        val Saver = Saver<WritePostState, Any>(
            save = { value ->
                listOf(
                    value.expanded,
                    with(TextFieldValue.Saver) { save(value.text) } ?: return@Saver null,
                )
            },
            restore = { list ->
                val (expanded, text) = list as List<*>
                WritePostState(
                    expanded = expanded as Boolean,
                    text = TextFieldValue.Saver.restore(text as Any) ?: return@Saver null,
                )
            }
        )
    }

    var expanded by mutableStateOf(expanded)
    var text by mutableStateOf(text)
}
