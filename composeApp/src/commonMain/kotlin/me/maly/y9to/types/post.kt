package me.maly.y9to.types

import kotlin.time.Instant


data class UiPost(
    val id: String,
    val author: UiPostAuthorPreview,
    val publishDate: Instant,
    val lastEditDate: Instant?,
    val content: UiPostContent,
)

sealed interface UiPostAuthorPreview {
    val firstName: String
    val lastName: String?

    data class User(
        val id: String,
        override val firstName: String,
        override val lastName: String?,
    ) : UiPostAuthorPreview

    data class DeletedUser(
        override val firstName: String,
        override val lastName: String?,
    ) : UiPostAuthorPreview

    val idOrNull get() = (this as? User)?.id

    val displayName get() =
        if (lastName == null) firstName
        else "$firstName $lastName"
}

sealed interface UiPostContent {
    data class Standalone(val text: String) : UiPostContent
    data class Repost(val comment: String?, val originalPreview: UiRepostPreview) : UiPostContent
}

sealed interface UiRepostPreview {
    data class Post(
        val id: String,
        val author: UiPostAuthorPreview,
        val publishDate: Instant,
        val lastEditDate: Instant?,
        val content: UiPostContent,
    ) : UiRepostPreview

    data class DeletedPost(
        val author: UiPostAuthorPreview,
        val deletionDate: Instant,
    ) : UiRepostPreview

    val idOrNull: String? get() = (this as? Post)?.id
}
