package me.maly.y9to.screen.feed

import me.maly.y9to.types.UiPost


data class UiFeedItem(
    val post: UiPost,
    val canEdit: Boolean,
    val canRepost: Boolean,
    val canDelete: Boolean,
    val canReply: Boolean,
)
