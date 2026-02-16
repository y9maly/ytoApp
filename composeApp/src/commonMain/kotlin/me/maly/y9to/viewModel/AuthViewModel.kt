package me.maly.y9to.viewModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthViewModel {
    val state: StateFlow<AuthUiState>
    val actions: Flow<AuthScreenAction>

    fun setPhoneNumber(value: String)
    fun setEmail(value: String)
    fun setUsername(value: String)
    fun setConfirmCode(value: String)
    fun setPassword(value: String)

    fun emitPhoneNumber()
    fun emitEmail()
    fun emitUsername()
    fun emitConfirmCode()
    fun emitPassword()
}