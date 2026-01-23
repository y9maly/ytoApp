package me.maly.y9to.screen.auth

import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState



enum class ConfirmCodeSource { PhoneNumber, Email }

sealed interface AuthScreenState : MVIState {
    data class Unauthenticated(
        val phoneNumberAvailable: Boolean,
        val emailAvailable: Boolean,
        val usernameAvailable: Boolean,
        val phoneNumber: String,
        val email: String,
        val username: String,
    ) : AuthScreenState

    data class ConfirmCode(
        val code: String,
        val source: ConfirmCodeSource,
        val length: Int,
    ) : AuthScreenState

    data class Password(
        val password: String,
        val hint: String?,
    ) : AuthScreenState

    data class Authorized(
        val firstName: String,
        val lastName: String,
    ) : AuthScreenState
}

sealed interface AuthScreenIntent : MVIIntent {
    data class ChanePhoneNumber(val phoneNumber: String) : AuthScreenIntent
    data class ChangeEmail(val email: String) : AuthScreenIntent
    data class ChaneUsername(val username: String) : AuthScreenIntent
    data class ChaneConfirmCode(val code: String) : AuthScreenIntent
    data class ChanePassword(val password: String) : AuthScreenIntent
    data object EmitPhoneNumber : AuthScreenIntent
    data object EmitEmail : AuthScreenIntent
    data object EmitUsername : AuthScreenIntent
    data object EmitConfirmCode : AuthScreenIntent
    data object EmitPassword : AuthScreenIntent
}

sealed interface AuthScreenAction : MVIAction {
    data class ShowDialog(val text: String) : AuthScreenAction
}
