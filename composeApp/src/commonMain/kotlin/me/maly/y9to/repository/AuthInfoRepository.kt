package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import y9to.api.types.AuthState


interface AuthInfoRepository {
    val authState: Flow<AuthState>
}

val AuthInfoRepository.isAuthenticated: Flow<Boolean>
    get() = authState.map { it is AuthState.Authorized }
