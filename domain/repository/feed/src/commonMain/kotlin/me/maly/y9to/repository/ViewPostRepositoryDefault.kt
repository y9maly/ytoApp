package me.maly.y9to.repository

import kotlinx.coroutines.flow.Flow
import y9to.api.types.InputPost
import y9to.api.types.Post
import y9to.sdk.Client


class ViewPostRepositoryDefault(
    private val client: Client,
) : ViewPostRepository {
    override suspend fun get(post: InputPost): Post? {
        return client.post.get(post)
    }

    override fun getFlow(post: InputPost): Flow<Post?> {
        return client.post.getFlow(post)
    }
}
