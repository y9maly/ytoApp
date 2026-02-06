package me.maly.y9to.types

import y9to.api.types.InputPost


sealed interface PostDraft {
    data class Standalone(
        val replyTo: InputPost,
        val text: String,
    ) : PostDraft

    data class Repost(
        val original: InputPost,
        val comment: String?,
    ) : PostDraft
}
