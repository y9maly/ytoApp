package me.maly.y9to.viewModel

import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview
import y9to.api.types.Post
import y9to.api.types.PostAuthorPreview
import y9to.api.types.PostContent
import y9to.api.types.RepostPreview


fun Post.map(): UiPost {
    return UiPost(
        id = id.long.toString(),
        author = UiPostAuthorPreview.User(
            id = author.id.long.toString(),
            firstName = author.firstName,
            lastName = author.lastName,
        ),
        publishDate = publishDate,
        lastEditDate = lastEditDate,
        content = content.map(),
    )
}

fun PostContent.map(): UiPostContent = when (this) {
    is PostContent.Standalone -> UiPostContent.Standalone(text)
    is PostContent.Repost -> UiPostContent.Repost(
        comment = comment,
        originalPreview = when (val preview = preview) {
            is RepostPreview.Post -> UiRepostPreview.Post(
                id = preview.postId.long.toString(),
                author = when (val author = preview.author) {
                    is PostAuthorPreview.User -> UiPostAuthorPreview.User(
                        id = author.id.toString(),
                        firstName = author.firstName,
                        lastName = author.lastName,
                    )

                    is PostAuthorPreview.DeletedUser -> UiPostAuthorPreview.DeletedUser(
                        firstName = author.firstName,
                        lastName = author.lastName,
                    )
                },
                publishDate = preview.publishDate,
                lastEditDate = preview.lastEditDate,
                content = preview.content.map(),
            )

            is RepostPreview.DeletedPost -> UiRepostPreview.DeletedPost(
                deletionDate = preview.deletionDate,
                author = when (val author = preview.author) {
                    is PostAuthorPreview.User -> UiPostAuthorPreview.User(
                        id = author.id.toString(),
                        firstName = author.firstName,
                        lastName = author.lastName,
                    )

                    is PostAuthorPreview.DeletedUser -> UiPostAuthorPreview.DeletedUser(
                        firstName = author.firstName,
                        lastName = author.lastName,
                    )
                },
            )
        }
    )
}

