package me.maly.y9to.screen.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import y9to.api.types.AuthState
import y9to.api.types.InputAuthMethod
import y9to.api.types.LogInError
import y9to.api.types.LogInOk
import y9to.api.types.LogInResult
import y9to.api.types.UserId
import y9to.libs.stdlib.asError
import y9to.libs.stdlib.asOk


class TestAuthRepository : AuthRepository {
    override val authState = MutableStateFlow<AuthState>(AuthState.Unauthorized)

    override suspend fun logIn(method: InputAuthMethod): LogInResult {
        delay(1000)

        return when (method) {
            is InputAuthMethod.Email -> when {
                "a" in method.email -> {
                    LogInError.UserForSpecifiedAuthMethodNotFound.asError()
                }

                else -> {
                    logIn()
                    LogInOk.asOk()
                }
            }

            is InputAuthMethod.PhoneNumber -> when {
                method.phoneNumber.startsWith("1") -> {
                    LogInError.UserForSpecifiedAuthMethodNotFound.asError()
                }

                else -> {
                    logIn()
                    LogInOk.asOk()
                }
            }
        }
    }

    private fun logIn() {
        authState.value = AuthState.Authorized(UserId(123))
    }
}
