package me.maly.y9to.screen.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun EnterPasswordScreen(
    password: TextFieldValue,
    hint: String?,
    onChangePassword: (TextFieldValue) -> Unit,
    onEmit: () -> Unit,
    loading: Boolean,
    invalidPassword: Boolean,
    modifier: Modifier = Modifier,
) {
    BaseEnterScreen(
        modifier = modifier,
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
            BaseEnterScreen.EmitResult.Ok
        },
    )
}
