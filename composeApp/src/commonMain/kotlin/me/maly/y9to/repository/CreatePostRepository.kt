package me.maly.y9to.repository

import y9to.api.types.CreatePostResult
import y9to.api.types.InputPost
import y9to.api.types.InputPostContent


interface CreatePostRepository {
    suspend fun create(
        replyTo: InputPost? = null,
        content: InputPostContent,
    ): CreatePostResult
}
