package me.maly.y9to.screen.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.EmptyContentPadding


@Composable
fun EnterPasswordScreen(
    password: TextFieldValue,
    hint: String?,
    onChangePassword: (TextFieldValue) -> Unit,
    onEmit: () -> Unit,
    loading: Boolean,
    invalidPassword: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    TemplateEnterScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        value = password,
        onChangeValue = onChangePassword,
        title = "Password",
        subtitle = "Enter your 2FA password",
        subsubtitle = "Password hint: $hint".takeIf { hint != null },
        label = { Text("Password") },
        prefix = null,
        loading = loading,
        errorText = "This password is invalid. Please try another one".takeIf { invalidPassword },
        fabEnabled = !invalidPassword,
        onEmit = onEmit@{
            onEmit()
            TemplateEnterScreen.EmitResult.Ok
        },
    )
}
