package me.maly.y9to.repository

import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInResult
import y9to.api.types.LogOutResult


interface AuthRepository {
    suspend fun logIn(method: InputAuthMethod): LogInResult

    suspend fun logOut(): LogOutResult
}
