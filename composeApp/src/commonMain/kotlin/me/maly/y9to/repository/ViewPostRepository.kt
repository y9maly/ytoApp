package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import y9to.api.types.InputPost
import y9to.api.types.Post


interface ViewPostRepository {
    suspend fun get(post: InputPost): Post?

    fun getFlow(post: InputPost): Flow<Post?>
}
