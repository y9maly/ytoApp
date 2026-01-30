package me.maly.y9to.compose.components.writePost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun rememberWritePostState(
    writeAccount: WriteAccount,
    text: TextFieldValue = TextFieldValue(),
    expanded: Boolean = false,
    onTextChange: WritePostState.(TextFieldValue) -> Unit = { this.text = it },
    onFocused: WritePostState.() -> Unit = { this.expanded = true },
    onCollapse: WritePostState.() -> Unit = { this.expanded = false },
    onPublish: WritePostState.() -> Unit = {},
) = remember {
    WritePostState(
        writeAccount = writeAccount,
        text = text,
        expanded = expanded,
        onTextChange = onTextChange,
        onFocused = onFocused,
        onCollapse = onCollapse,
        onPublish = onPublish,
    )
}.apply {
    this.writeAccount = writeAccount
    this.text = text
    this.expanded = expanded
    this.onTextChange = onTextChange
    this.onFocused = onFocused
    this.onCollapse = onCollapse
    this.onPublish = onPublish
}

class WritePostState(
    writeAccount: WriteAccount,
    text: TextFieldValue,
    expanded: Boolean,
    internal var onTextChange: WritePostState.(TextFieldValue) -> Unit,
    internal var onFocused: WritePostState.() -> Unit,
    internal var onCollapse: WritePostState.() -> Unit,
    internal var onPublish: WritePostState.() -> Unit,
) {
    var text by mutableStateOf(text)
    var expanded by mutableStateOf(expanded)
    var writeAccount by mutableStateOf(writeAccount)

    fun onTextChange(value: TextFieldValue) = onTextChange.invoke(this, value)
    fun onFocused() = onFocused.invoke(this)
    fun onCollapse() = onCollapse.invoke(this)
    fun onPublish() = onPublish.invoke(this)
}
