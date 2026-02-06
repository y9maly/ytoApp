package me.maly.y9to.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import y9to.api.types.AuthState
import y9to.libs.stdlib.optional.Optional
import y9to.libs.stdlib.optional.none
import y9to.libs.stdlib.optional.present


interface AuthStore {
    suspend fun saveAuthState(value: AuthState)
    suspend fun readAuthState(): Optional<AuthState>
    suspend fun collectAuthState(): Flow<Optional<AuthState>>
}

class InMemoryAuthStore : AuthStore {
    private val authState = MutableStateFlow(none<AuthState>())

    override suspend fun readAuthState() = authState.value
    override suspend fun collectAuthState() = authState.asStateFlow()
    override suspend fun saveAuthState(value: AuthState) {
        authState.value = present(value)
    }
}
