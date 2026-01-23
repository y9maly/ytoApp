package me.maly.y9to.screen.auth

import androidx.compose.runtime.Composable


object AuthScreen {
    sealed interface State {
        data object Loading : State

        data class Active(
            val loginState: LoginState,
            val invalidPhoneNumber: Boolean = false,
            val invalidEmail: Boolean = false,
            val processing: Boolean = false,
        ) : State

        data class Authenticated(val firstName: String, val lastName: String) : State
    }

    sealed interface LoginState {
        data object WaitPhoneNumberOrEmail : LoginState
        data class WaitConfirmCode(val length: Int) : LoginState
    }

    sealed interface EmitPhoneNumberResult {
        data object Ok : EmitPhoneNumberResult
        data object Unregistered : EmitPhoneNumberResult
        data object Banned : EmitPhoneNumberResult
    }
}
