package me.maly.y9to.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.LambdaIntent
import pro.respawn.flowmvi.dsl.reduceLambdas
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.dsl.withStateOrThrow
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInError
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.libs.stdlib.coroutines.flow.firstNotNull
import y9to.sdk.Client


interface AuthComponent {
    val store: Store<AuthScreenState, AuthScreenIntent, AuthScreenAction>
}

private typealias Ctx = PipelineContext<AuthScreenState, AuthScreenIntent, AuthScreenAction>

class AuthComponentDefault(private val client: Client) : ViewModel(), AuthComponent {
    override val store = store<AuthScreenState, AuthScreenIntent, AuthScreenAction>(
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
        ),
        viewModelScope
    ) {
        whileSubscribed {
            client.auth.authState.collectIn(viewModelScope) { authState ->
                when (authState) {
                    is AuthState.Authorized -> {
                        val me = client.user.me.firstNotNull()
                        updateState {
                            AuthScreenState.Authorized(
                                firstName = me.firstName,
                                lastName = me.lastName,
                                loading = false
                            )
                        }
                    }

                    AuthState.Unauthorized -> {
                        updateState {
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
                        }
                    }
                }
            }
        }

        reduce { intent ->
            when (intent) {
                is AuthScreenIntent.ChangeEmail -> updateState<AuthScreenState.Unauthenticated, _> {
                    copy(email = intent.email)
                }

                is AuthScreenIntent.ChangeUsername -> updateState<AuthScreenState.Unauthenticated, _> {
                    copy(username = intent.username)
                }

                is AuthScreenIntent.ChangePhoneNumber -> updateState<AuthScreenState.Unauthenticated, _> {
                    copy(phoneNumber = intent.phoneNumber)
                }

                is AuthScreenIntent.ChangeConfirmCode -> updateState<AuthScreenState.ConfirmCode, _> {
                    copy(code = intent.code)
                }

                is AuthScreenIntent.ChangePassword -> updateState<AuthScreenState.Password, _> {
                    copy(password = intent.password)
                }

                is AuthScreenIntent.EmitEmail -> withState<AuthScreenState.Unauthenticated, _> {
                    emitEmail(phoneNumber)
                }

                is AuthScreenIntent.EmitUsername -> withState<AuthScreenState.Unauthenticated, _> {
                    emitPhoneNumber(phoneNumber)
                }

                is AuthScreenIntent.EmitPhoneNumber -> withState<AuthScreenState.Unauthenticated, _> {
                    emitPhoneNumber(phoneNumber)
                }

                is AuthScreenIntent.EmitConfirmCode -> withState<AuthScreenState.ConfirmCode, _> {
                    emitConfirmCode(code)
                }

                is AuthScreenIntent.EmitPassword -> withState<AuthScreenState.Password, _> {
                    emitPassword(password)
                }
            }
        }
    }

    private fun Ctx.emitEmail(email: String) = viewModelScope.launch {
        updateState<AuthScreenState.Unauthenticated, _> {
            copy(loading = true)
        }

        val result = client.auth.logIn(InputAuthMethod.Email(email))

        result.onError { error ->
            when (error) {
                LogInError.AlreadyLogInned -> {}
                LogInError.UserForSpecifiedAuthMethodNotFound -> updateState<AuthScreenState.Unauthenticated, _> {
                    copy(invalidEmails = invalidEmails + email)
                }
            }
        }

        updateState<AuthScreenState.Unauthenticated, _> {
            copy(loading = false)
        }
    }

    private fun Ctx.emitPhoneNumber(phoneNumber: String) = viewModelScope.launch {
        updateState<AuthScreenState.Unauthenticated, _> {
            copy(loading = true)
        }

        val result = client.auth.logIn(InputAuthMethod.PhoneNumber(phoneNumber))

        result.onError { error ->
            when (error) {
                LogInError.AlreadyLogInned -> {}
                LogInError.UserForSpecifiedAuthMethodNotFound -> updateState<AuthScreenState.Unauthenticated, _> {
                    copy(invalidPhoneNumbers = invalidPhoneNumbers + phoneNumber)
                }
            }
        }

        updateState<AuthScreenState.Unauthenticated, _> {
            copy(loading = false)
        }
    }

    private fun emitConfirmCode(code: String) = viewModelScope.launch {

    }

    private fun emitPassword(password: String) = viewModelScope.launch {

    }
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

class AuthComponentTest : AuthComponent {
    override val store = testStore()
}
