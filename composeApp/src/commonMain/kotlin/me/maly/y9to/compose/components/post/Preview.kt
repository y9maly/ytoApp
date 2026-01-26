package me.maly.y9to.compose.components.post

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview
import kotlin.time.Clock


private val standalone = UiPost(
    id = "",
    author = UiPostAuthorPreview.User(
        id = "",
        firstName = "test",
        lastName = "test"
    ),
    publishDate = Clock.System.now(),
    lastEditDate = null,
    content = UiPostContent.Standalone("Hello!")
)

private val repost = standalone.copy(
    content = UiPostContent.Repost(
        comment = "Hi",
        originalPreview = UiRepostPreview.Post(
            id = standalone.id,
            author = standalone.author,
            publishDate = standalone.publishDate,
            lastEditDate = null,
            content = standalone.content,
        )
    )
)

private val repostRepost = standalone.copy(
    content = UiPostContent.Repost(
        comment = "Hi again",
        originalPreview = UiRepostPreview.Post(
            id = repost.id,
            author = repost.author,
            publishDate = repost.publishDate,
            lastEditDate = null,
            content = repost.content,
        )
    )
)

private val repostDeleted = standalone.copy(
    content = UiPostContent.Repost(
        comment = "Hi again",
        originalPreview = UiRepostPreview.DeletedPost(
            author = repost.author,
            deletionDate = repost.publishDate,
        )
    )
)

@Preview
@Composable
private fun Standalone() {
    PostCard(post = standalone)
}

@Preview
@Composable
private fun Repost() {
    PostCard(post = repost)
}

@Preview
@Composable
private fun RepostRepost() {
    PostCard(post = repostRepost)
}

@Preview
@Composable
private fun RepostDeleted() {
    PostCard(post = repostDeleted)
}
