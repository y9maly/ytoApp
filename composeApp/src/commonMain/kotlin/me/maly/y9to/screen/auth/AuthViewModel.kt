package me.maly.y9to.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import me.maly.y9to.screen.auth.AuthScreen.LoginState
import me.maly.y9to.screen.auth.AuthScreen.State
import me.maly.y9to.types.UiLoginState
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInError
import y9to.libs.stdlib.coroutines.flow.collectIn
import y9to.libs.stdlib.successOrElse


@Suppress("ReturnInsideFinallyBlock")
class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        authRepository.authState.collectIn(viewModelScope) { authState ->
            _state.value = when (authState) {
                is AuthState.Authorized -> State.Authenticated("Test", "Test")

                is AuthState.Unauthorized -> State.Active(
                    loginState = LoginState.WaitPhoneNumberOrEmail
                )
            }
        }
    }

    fun resetInvalidPhoneNumber() {
        _state.update { currentState ->
            if (currentState !is State.Active)
                return
            currentState.copy(invalidPhoneNumber = false)
        }
    }

    fun resetInvalidEmail() {
        _state.update { currentState ->
            if (currentState !is State.Active)
                return
            currentState.copy(invalidEmail = false)
        }
    }

    fun emitPhoneNumber(phoneNumber: String) {
        emit(InputAuthMethod.PhoneNumber(phoneNumber))
    }

    fun emitEmail(email: String) {
        emit(InputAuthMethod.Email(email))
    }

    private fun markPhoneNumberInvalid() {
        _state.update { currentState ->
            if (currentState !is State.Active) return
            currentState.copy(invalidPhoneNumber = true)
        }
    }

    private fun markEmailInvalid() {
        _state.update { currentState ->
            if (currentState !is State.Active) return
            currentState.copy(invalidEmail = true)
        }
    }

    private fun emit(method: InputAuthMethod) {
        _state.update { currentState ->
            if (currentState !is State.Active)
                return
            if (currentState.loginState !is LoginState.WaitPhoneNumberOrEmail)
                return
            if (currentState.processing)
                return
            currentState.copy(processing = true)
        }

        viewModelScope.launch {
            try {
                authRepository.logIn(method).successOrElse { error ->
                    when (error) {
                        LogInError.AlreadyLogInned -> { /* do nothing */
                        }

                        LogInError.UserForSpecifiedAuthMethodNotFound -> {
                            when (method) {
                                is InputAuthMethod.PhoneNumber -> markPhoneNumberInvalid()
                                is InputAuthMethod.Email -> markEmailInvalid()
                            }
                        }
                    }
                }
            } finally {
                _state.update { currentState ->
                    if (currentState !is State.Active)
                        return@launch
                    currentState.copy(processing = false)
                }
            }
        }
    }
}
