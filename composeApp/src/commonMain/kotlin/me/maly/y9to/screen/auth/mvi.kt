package me.maly.y9to.screen.auth

import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState


enum class ConfirmCodeSource { PhoneNumber, Email }

sealed interface AuthUiState : MVIState {
    val loading: Boolean

    data class Unauthenticated(
        val phoneNumberAvailable: Boolean,
        val emailAvailable: Boolean,
        val usernameAvailable: Boolean,
        val invalidPhoneNumbers: Set<String>,
        val invalidEmails: Set<String>,
        val invalidUsernames: Set<String>,
        val phoneNumber: String,
        val email: String,
        val username: String,
        override val loading: Boolean,
    ) : AuthUiState

    data class ConfirmCode(
        val code: String,
        val invalidCodes: Set<String>,
        val source: ConfirmCodeSource,
        val length: Int,
        override val loading: Boolean,
    ) : AuthUiState

    data class Password(
        val password: String,
        val triesLeft: Int,
        val invalidPasswords: Set<String>,
        val hint: String?,
        override val loading: Boolean,
    ) : AuthUiState

    data class Authorized(
        val firstName: String,
        val lastName: String?,
        override val loading: Boolean,
    ) : AuthUiState
}

sealed interface AuthScreenIntent : MVIIntent {
    data class ChangePhoneNumber(val phoneNumber: String) : AuthScreenIntent
    data class ChangeEmail(val email: String) : AuthScreenIntent
    data class ChangeUsername(val username: String) : AuthScreenIntent
    data class ChangeConfirmCode(val code: String) : AuthScreenIntent
    data class ChangePassword(val password: String) : AuthScreenIntent
    data object EmitPhoneNumber : AuthScreenIntent
    data object EmitEmail : AuthScreenIntent
    data object EmitUsername : AuthScreenIntent
    data object EmitConfirmCode : AuthScreenIntent
    data object EmitPassword : AuthScreenIntent
}

sealed interface AuthScreenAction : MVIAction {
    data class ShowDialog(val text: String) : AuthScreenAction
}
