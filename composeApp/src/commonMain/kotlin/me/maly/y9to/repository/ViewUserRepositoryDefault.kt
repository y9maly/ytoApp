package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import me.maly.y9to.types.UiProfile
import y9to.api.types.User
import y9to.api.types.UserId
import y9to.sdk.Client


class ViewUserRepositoryDefault(private val client: Client) : ViewUserRepository {
    override suspend fun get(id: UserId): User? {
        return client.user.get(id)
    }

    override fun getFlow(id: UserId): Flow<User?> {
        return client.user.getFlow(id)
    }
}
