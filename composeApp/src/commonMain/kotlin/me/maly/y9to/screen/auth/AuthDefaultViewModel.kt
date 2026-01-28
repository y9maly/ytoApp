package me.maly.y9to.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInError
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.libs.stdlib.coroutines.flow.firstNotNull
import y9to.sdk.Client


private typealias Ctx = PipelineContext<AuthScreenState, AuthScreenIntent, AuthScreenAction>


class AuthDefaultViewModel(private val client: Client) : ViewModel(), AuthViewModel {
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
                        val me = client.user.myProfile.firstNotNull()
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
