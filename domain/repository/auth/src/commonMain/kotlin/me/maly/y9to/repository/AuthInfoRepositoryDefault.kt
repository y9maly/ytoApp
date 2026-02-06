package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import y9to.api.types.AuthState
import y9to.sdk.Client


class AuthInfoRepositoryDefault(
    private val client: Client,
//    private val store: AuthStore,
) : AuthInfoRepository {
    override val authState: Flow<AuthState> = client.auth.authState
}
