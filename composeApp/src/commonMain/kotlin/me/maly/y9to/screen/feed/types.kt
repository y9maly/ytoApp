package me.maly.y9to.screen.feed

import me.maly.y9to.screen.feed.UiPostPrePublishPreview.Pending
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import kotlin.time.Instant


sealed interface UiInputPostContent {
    data class Standalone(val text: String) : UiInputPostContent
}

sealed interface UiPostPrePublishPreview {
    val author: UiPostAuthorPreview? // null if loading
    val publishDate: Instant
    val content: UiPostContent

    data class Pending(
        override val author: UiPostAuthorPreview?,
        override val publishDate: Instant,
        override val content: UiPostContent,
    ) : UiPostPrePublishPreview

    data class Error(
        override val author: UiPostAuthorPreview?,
        override val publishDate: Instant,
        override val content: UiPostContent,
        val errorMessage: String,
    ) : UiPostPrePublishPreview
}

fun UiPostPrePublishPreview.copy(
    author: UiPostAuthorPreview? = this.author,
    publishDate: Instant = this.publishDate,
    content: UiPostContent = this.content,
) = when (this) {
    is Pending -> copy(author = author, publishDate = publishDate, content = content)
    is UiPostPrePublishPreview.Error -> copy(author = author, publishDate = publishDate, content = content)
}

fun UiPostPrePublishPreview.copyAsError(
    author: UiPostAuthorPreview? = this.author,
    publishDate: Instant = this.publishDate,
    content: UiPostContent = this.content,
    errorMessage: String
) = UiPostPrePublishPreview.Error(
    author = author,
    publishDate = publishDate,
    content = content,
    errorMessage = errorMessage,
)
