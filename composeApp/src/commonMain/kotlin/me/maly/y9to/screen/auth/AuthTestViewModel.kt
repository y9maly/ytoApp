package me.maly.y9to.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.reduce


class AuthTestViewModel : ViewModel(), AuthViewModel {
    override val store = testStore()
        .apply { start(viewModelScope) }
}


private fun testStore() = store<AuthScreenState, AuthScreenIntent, AuthScreenAction>(
    AuthScreenState.Unauthenticated(
        phoneNumberAvailable = true,
        emailAvailable = true,
        usernameAvailable = true,
        invalidPhoneNumbers = emptySet(),
        invalidUsernames = emptySet(),
        invalidEmails = emptySet(),
        phoneNumber = "7",
        email = "",
        username = "",
        loading = false,
    )
) {
    reduce { intent ->
        when (intent) {
            is AuthScreenIntent.ChangeEmail -> updateStateImmediate<AuthScreenState.Unauthenticated, _> {
                copy(email = intent.email)
            }

            is AuthScreenIntent.ChangeUsername -> updateStateImmediate<AuthScreenState.Unauthenticated, _> {
                copy(username = intent.username)
            }

            is AuthScreenIntent.ChangePhoneNumber -> updateStateImmediate<AuthScreenState.Unauthenticated, _> {
                copy(phoneNumber = intent.phoneNumber)
            }

            is AuthScreenIntent.ChangeConfirmCode -> updateStateImmediate<AuthScreenState.ConfirmCode, _> {
                copy(code = intent.code)
            }

            is AuthScreenIntent.ChangePassword -> updateStateImmediate<AuthScreenState.Password, _> {
                copy(password = intent.password)
            }

            is AuthScreenIntent.EmitEmail,
            is AuthScreenIntent.EmitPhoneNumber,
            is AuthScreenIntent.EmitUsername -> updateState<AuthScreenState.Unauthenticated, _> {
                updateState<AuthScreenState.Unauthenticated, _> {
                    copy(loading = true)
                }

                delay((300L..1000L).random())

                when (intent) {
                    is AuthScreenIntent.EmitEmail if email.length <= 5 -> return@updateState copy(
                        loading = false,
                        invalidEmails = invalidEmails + email
                    )

                    is AuthScreenIntent.EmitUsername if username.length <= 3 -> return@updateState copy(
                        loading = false,
                        invalidUsernames = invalidUsernames + username
                    )

                    is AuthScreenIntent.EmitPhoneNumber if phoneNumber.length <= 3 -> return@updateState copy(
                        loading = false,
                        invalidPhoneNumbers = invalidPhoneNumbers + phoneNumber
                    )

                    else -> {}
                }

                AuthScreenState.ConfirmCode(
                    code = "",
                    invalidCodes = emptySet(),
                    source = when (intent) {
                        is AuthScreenIntent.EmitEmail -> ConfirmCodeSource.Email
                        is AuthScreenIntent.EmitUsername,
                        is AuthScreenIntent.EmitPhoneNumber -> ConfirmCodeSource.PhoneNumber

                        else -> error("Unreachable")
                    },
                    length = when (intent) {
                        is AuthScreenIntent.EmitEmail -> 6
                        is AuthScreenIntent.EmitUsername -> 4
                        is AuthScreenIntent.EmitPhoneNumber -> 6
                        else -> error("Unreachable")
                    },
                    loading = false,
                )
            }

            AuthScreenIntent.EmitConfirmCode -> updateState<AuthScreenState.ConfirmCode, _> {
                updateState<AuthScreenState.ConfirmCode, _> {
                    copy(loading = true)
                }

                delay((300L..1000L).random())

                if (code.length != length && code != "123")
                    copy(loading = false, invalidCodes = invalidCodes + code)
                else
                    AuthScreenState.Password(
                        password = "",
                        invalidPasswords = emptySet(),
                        triesLeft = 5,
                        hint = "$code is valid!",
                        loading = false,
                    )
            }

            AuthScreenIntent.EmitPassword -> updateState<AuthScreenState.Password, _> {
                updateState<AuthScreenState.Password, _> {
                    copy(loading = true)
                }

                delay((300L..1000L).random())

                if (password.length <= 3)
                    copy(loading = false, invalidPasswords = invalidPasswords + password)
                else
                    AuthScreenState.Authorized(
                        firstName = "First name",
                        lastName = password,
                        loading = false,
                    )
            }
        }
    }
}
