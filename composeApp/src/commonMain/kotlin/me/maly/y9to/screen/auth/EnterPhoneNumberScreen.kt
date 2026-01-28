package me.maly.y9to.screen.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun EnterPhoneNumberScreen(
    phoneNumber: TextFieldValue,
    onChangePhoneNumber: (TextFieldValue) -> Unit,
    onEmit: () -> Unit,
    loading: Boolean,
    invalidPhoneNumber: Boolean,
    modifier: Modifier = Modifier,
) {
    TemplateEnterScreen(
        modifier = modifier,
        value = phoneNumber,
        onChangeValue = { onChangePhoneNumber(it.copy(text = it.text.filter { it.isDigit() })) },
        title = "Your phone number",
        subtitle = "You can use any sequence of numbers. It will be public.",
        subsubtitle = null,
        label = { Text("Phone number") },
        prefix = { Text("+") },
        loading = loading,
        errorText = "This phone number is invalid".takeIf { invalidPhoneNumber },
        fabEnabled = !invalidPhoneNumber,
        onEmit = onEmit@{ confirmed ->
            if (!confirmed)
                return@onEmit TemplateEnterScreen.EmitResult.ShowDialog(TemplateEnterScreen.DialogProperties(
                    title = "Is this the correct number?",
                    message = phoneNumber.text,
                    confirm = "Yes",
                    dismiss = "No, change the number"
                ))
            onEmit()
            TemplateEnterScreen.EmitResult.Ok
        },
    )
}
