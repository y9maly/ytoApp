package me.maly.y9to.repository

import y9to.api.types.CreatePostResult
import y9to.api.types.InputPost
import y9to.api.types.InputPostContent
import y9to.sdk.Client


class CreatePostRepositoryDefault(
    private val client: Client,
) : CreatePostRepository {
    override suspend fun create(
        replyTo: InputPost?,
        content: InputPostContent
    ): CreatePostResult {
        return client.post.create(replyTo, content)
    }
}
