package me.maly.y9to.repository

import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInResult
import y9to.api.types.LogOutResult
import y9to.sdk.Client


class AuthRepositoryDefault(
    private val client: Client,
) : AuthRepository {
    override suspend fun logIn(method: InputAuthMethod): LogInResult {
        return client.auth.logIn(method)
    }

    override suspend fun logOut(): LogOutResult {
        return client.auth.logOut()
    }
}
