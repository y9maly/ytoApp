package me.maly.y9to.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import pro.respawn.flowmvi.compose.dsl.subscribe
import pro.respawn.flowmvi.util.typed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val store by rememberUpdatedState(vm.store)
    var dialog by remember { mutableStateOf<String?>(null) }

    val state by store.subscribe { action ->
        when (action) {
            is AuthScreenAction.ShowDialog -> {
                dialog = action.text
            }
        }
    }

    val currentPhoneNumber = state.typed<AuthUiState.Unauthenticated>()?.phoneNumber
    val currentConfirmCode = state.typed<AuthUiState.ConfirmCode>()?.code
    val currentPassword = state.typed<AuthUiState.Password>()?.password

    var phoneNumber by remember { mutableStateOf(TextFieldValue(currentPhoneNumber ?: "")) }
    var confirmCode by remember { mutableStateOf(TextFieldValue(currentConfirmCode ?: "")) }
    var password by remember { mutableStateOf(TextFieldValue(currentPassword ?: "")) }

    LaunchedEffect(currentPhoneNumber) {
        phoneNumber = phoneNumber.copy(text = currentPhoneNumber ?: return@LaunchedEffect)
    }

    LaunchedEffect(currentConfirmCode) {
        confirmCode = confirmCode.copy(text = currentConfirmCode ?: return@LaunchedEffect)
    }

    LaunchedEffect(currentPassword) {
        password = password.copy(text = currentPassword ?: return@LaunchedEffect)
    }

    AnimatedContent(state, modifier, contentKey = { it::class }) { state ->
        when (state) {
            is AuthUiState.Unauthenticated -> {
                EnterPhoneNumberScreen(
                    modifier = Modifier.fillMaxSize(),
                    phoneNumber = phoneNumber,
                    onChangePhoneNumber = {
                        phoneNumber = it
                        store.intent(AuthScreenIntent.ChangePhoneNumber(phoneNumber.text))
                    },
                    loading = state.loading,
                    invalidPhoneNumber = phoneNumber.text in state.invalidPhoneNumbers,
                    onEmit = {
                        store.intent(AuthScreenIntent.EmitPhoneNumber)
                    },
                )
            }

            is AuthUiState.ConfirmCode -> {
                EnterConfirmCodeScreen(
                    modifier = Modifier.fillMaxSize(),
                    code = confirmCode,
                    onChangeCode = {
                        confirmCode = it
                        store.intent(AuthScreenIntent.ChangeConfirmCode(confirmCode.text))
                    },
                    loading = state.loading,
                    invalidCode = confirmCode.text in state.invalidCodes,
                    codeSourceText = when (state.source) {
                        ConfirmCodeSource.PhoneNumber -> "Please check your messages"
                        ConfirmCodeSource.Email -> "Please check your email ans SPAM folder"
                    },
                    onEmit = {
                        store.intent(AuthScreenIntent.EmitConfirmCode)
                    },
                )
            }

            is AuthUiState.Password -> {
                EnterPasswordScreen(
                    modifier = Modifier.fillMaxSize(),
                    password = password,
                    onChangePassword = {
                        password = it
                        store.intent(AuthScreenIntent.ChangePassword(password.text))
                    },
                    hint = state.hint,
                    loading = state.loading,
                    invalidPassword = password.text in state.invalidPasswords,
                    onEmit = {
                        store.intent(AuthScreenIntent.EmitPassword)
                    },
                )
            }
        }
    }

    if (dialog != null) {
        BasicAlertDialog({ dialog = null }) {
            Text(dialog ?: "")
        }
    }
}
