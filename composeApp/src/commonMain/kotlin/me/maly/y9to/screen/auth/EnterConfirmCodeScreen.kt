package me.maly.y9to.screen.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.EmptyContentPadding

@Composable
fun EnterConfirmCodeScreen(
    code: TextFieldValue,
    codeSourceText: String?,
    onChangeCode: (TextFieldValue) -> Unit,
    onEmit: () -> Unit,
    loading: Boolean,
    invalidCode: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    TemplateEnterScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        value = code,
        onChangeValue = { onChangeCode(it.copy(text = it.text.filter { it.isDigit() })) },
        title = "Confirm code",
        subtitle = "(Говорят, что 123 всегда подходит)",
        subsubtitle = codeSourceText,
        label = { Text("Your confirm code") },
        prefix = null,
        loading = loading,
        errorText = "This code is invalid. Please try another one".takeIf { invalidCode },
        fabEnabled = !invalidCode,
        onEmit = onEmit@{
            onEmit()
            TemplateEnterScreen.EmitResult.Ok
        },
    )
}
