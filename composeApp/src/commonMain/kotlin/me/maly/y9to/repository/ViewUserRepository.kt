package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import me.maly.y9to.types.UiProfile
import y9to.api.types.User
import y9to.api.types.UserId


interface ViewUserRepository {
    suspend fun get(id: UserId): User?
    fun getFlow(id: UserId): Flow<User?>
}
