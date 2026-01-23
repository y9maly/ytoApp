package me.maly.y9to.screen.auth

import kotlinx.coroutines.flow.Flow
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInResult


interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun logIn(method: InputAuthMethod): LogInResult
}
