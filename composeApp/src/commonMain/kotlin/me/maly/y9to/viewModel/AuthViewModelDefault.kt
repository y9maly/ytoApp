package me.maly.y9to.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.maly.y9to.repository.AuthInfoRepository
import me.maly.y9to.repository.AuthRepository
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.subscribe
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.withState
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInError
import y9to.libs.stdlib.coroutines.flow.collectIn


private typealias Ctx = PipelineContext<AuthUiState, AuthScreenIntent, AuthScreenAction>


class AuthDefaultViewModel(
    private val authInfoRepository: AuthInfoRepository,
    private val authRepository: AuthRepository,
) : ViewModel(), AuthViewModel {
    override val state = MutableStateFlow<AuthUiState>(
        AuthUiState.Unauthenticated(
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
    )

    override val actions = MutableSharedFlow<AuthScreenAction>()

    override fun setPhoneNumber(value: String) {
        store.intent(AuthScreenIntent.ChangePhoneNumber(value))
    }

    override fun setEmail(value: String) {
        store.intent(AuthScreenIntent.ChangeEmail(value))
    }

    override fun setUsername(value: String) {
        store.intent(AuthScreenIntent.ChangeUsername(value))
    }

    override fun setConfirmCode(value: String) {
        store.intent(AuthScreenIntent.ChangeConfirmCode(value))
    }

    override fun setPassword(value: String) {
        store.intent(AuthScreenIntent.ChangePassword(value))
    }

    override fun emitPhoneNumber() {
        store.intent(AuthScreenIntent.EmitPhoneNumber)
    }

    override fun emitEmail() {
        store.intent(AuthScreenIntent.EmitEmail)
    }

    override fun emitUsername() {
        store.intent(AuthScreenIntent.EmitUsername)
    }

    override fun emitConfirmCode() {
        store.intent(AuthScreenIntent.EmitConfirmCode)
    }

    override fun emitPassword() {
        store.intent(AuthScreenIntent.EmitPassword)
    }

    private val store = store<AuthUiState, AuthScreenIntent, AuthScreenAction>(
        state.value,
        viewModelScope
    ) {
        whileSubscribed {
            authInfoRepository.authState.collectIn(viewModelScope) { authState ->
                when (authState) {
                    is AuthState.Authorized -> {
                        // do nothing
                    }

                    AuthState.Unauthorized -> {
                        updateState {
                            AuthUiState.Unauthenticated(
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
                is AuthScreenIntent.ChangeEmail -> updateState<AuthUiState.Unauthenticated, _> {
                    copy(email = intent.email)
                }

                is AuthScreenIntent.ChangeUsername -> updateState<AuthUiState.Unauthenticated, _> {
                    copy(username = intent.username)
                }

                is AuthScreenIntent.ChangePhoneNumber -> updateState<AuthUiState.Unauthenticated, _> {
                    copy(phoneNumber = intent.phoneNumber)
                }

                is AuthScreenIntent.ChangeConfirmCode -> updateState<AuthUiState.ConfirmCode, _> {
                    copy(code = intent.code)
                }

                is AuthScreenIntent.ChangePassword -> updateState<AuthUiState.Password, _> {
                    copy(password = intent.password)
                }

                is AuthScreenIntent.EmitEmail -> withState<AuthUiState.Unauthenticated, _> {
                    emitEmail(phoneNumber)
                }

                is AuthScreenIntent.EmitUsername -> withState<AuthUiState.Unauthenticated, _> {
                    emitPhoneNumber(phoneNumber)
                }

                is AuthScreenIntent.EmitPhoneNumber -> withState<AuthUiState.Unauthenticated, _> {
                    emitPhoneNumber(phoneNumber)
                }

                is AuthScreenIntent.EmitConfirmCode -> withState<AuthUiState.ConfirmCode, _> {
                    emitConfirmCode(code)
                }

                is AuthScreenIntent.EmitPassword -> withState<AuthUiState.Password, _> {
                    emitPassword(password)
                }
            }
        }
    }

    init {
        viewModelScope.subscribe(
            store = store,
            consume = { actions.emit(it) },
            render = { state.value = it }
        )
    }

    private fun Ctx.emitEmail(email: String) = viewModelScope.launch {
        updateState<AuthUiState.Unauthenticated, _> {
            copy(loading = true)
        }

        val result = authRepository.logIn(InputAuthMethod.Email(email))

        result.onError { error ->
            when (error) {
                LogInError.AlreadyLogInned -> {}
                LogInError.UserForSpecifiedAuthMethodNotFound -> updateState<AuthUiState.Unauthenticated, _> {
                    copy(invalidEmails = invalidEmails + email)
                }
            }
        }

        updateState<AuthUiState.Unauthenticated, _> {
            copy(loading = false)
        }
    }

    private fun Ctx.emitPhoneNumber(phoneNumber: String) = viewModelScope.launch {
        updateState<AuthUiState.Unauthenticated, _> {
            copy(loading = true)
        }

        val result = authRepository.logIn(InputAuthMethod.PhoneNumber(phoneNumber))

        result.onError { error ->
            when (error) {
                LogInError.AlreadyLogInned -> {}
                LogInError.UserForSpecifiedAuthMethodNotFound -> updateState<AuthUiState.Unauthenticated, _> {
                    copy(invalidPhoneNumbers = invalidPhoneNumbers + phoneNumber)
                }
            }
        }

        updateState<AuthUiState.Unauthenticated, _> {
            copy(loading = false)
        }
    }

    private fun emitConfirmCode(code: String) = viewModelScope.launch {

    }

    private fun emitPassword(password: String) = viewModelScope.launch {

    }
}
