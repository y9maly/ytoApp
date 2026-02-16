package me.maly.y9to.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import me.maly.y9to.viewModel.AuthScreenAction
import me.maly.y9to.viewModel.AuthUiState
import me.maly.y9to.viewModel.AuthViewModel
import me.maly.y9to.viewModel.ConfirmCodeSource
import pro.respawn.flowmvi.util.typed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    var dialog by remember { mutableStateOf<String?>(null) }
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.actions.collect { action ->
            when (action) {
                is AuthScreenAction.ShowDialog -> {
                    dialog = action.text
                }
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
                        vm.setPhoneNumber(phoneNumber.text)
                    },
                    loading = state.loading,
                    invalidPhoneNumber = phoneNumber.text in state.invalidPhoneNumbers,
                    onEmit = {
                        vm.emitPhoneNumber()
                    },
                )
            }

            is AuthUiState.ConfirmCode -> {
                EnterConfirmCodeScreen(
                    modifier = Modifier.fillMaxSize(),
                    code = confirmCode,
                    onChangeCode = {
                        confirmCode = it
                        vm.setConfirmCode(it.text)
                    },
                    loading = state.loading,
                    invalidCode = confirmCode.text in state.invalidCodes,
                    codeSourceText = when (state.source) {
                        ConfirmCodeSource.PhoneNumber -> "Please check your messages"
                        ConfirmCodeSource.Email -> "Please check your email ans SPAM folder"
                    },
                    onEmit = {
                        vm.emitConfirmCode()
                    },
                )
            }

            is AuthUiState.Password -> {
                EnterPasswordScreen(
                    modifier = Modifier.fillMaxSize(),
                    password = password,
                    onChangePassword = {
                        password = it
                        vm.setPassword(it.text)
                    },
                    hint = state.hint,
                    loading = state.loading,
                    invalidPassword = password.text in state.invalidPasswords,
                    onEmit = {
                        vm.emitPassword()
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
