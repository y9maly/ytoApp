package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiPostTerminateAction
import kotlin.time.Instant


@Composable
fun PostCard(
    post: UiPost,
    modifier: Modifier = Modifier,
    gotoProfile: (String) -> Unit = {},
    gotoPostDetails: (String) -> Unit = {},
) = PostCard(
    author = post.author,
    publishDate = post.publishDate,
    lastEditDate = post.lastEditDate,
    content = post.content,
    modifier = modifier,
    gotoProfile = gotoProfile,
    gotoPostDetails = gotoPostDetails,
)

@Composable
fun PostCard(
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    lastEditDate: Instant?,
    content: UiPostContent,
    modifier: Modifier = Modifier,
    gotoProfile: (String) -> Unit = {},
    gotoPostDetails: (String) -> Unit = {},
) {
    OutlinedCard(modifier) {
        Post(
            author = author,
            publishDate = publishDate,
            lastEditDate = lastEditDate,
            content = content,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            gotoProfile = gotoProfile,
            gotoPostDetails = gotoPostDetails,
        )
    }
}


@Composable
fun Post(
    post: UiPost,
    modifier: Modifier = Modifier,
    gotoProfile: (String) -> Unit = {},
    gotoPostDetails: (String) -> Unit = {},
) = Post(
    author = post.author,
    publishDate = post.publishDate,
    lastEditDate = post.lastEditDate,
    content = post.content,
    modifier = modifier,
    gotoProfile = gotoProfile,
    gotoPostDetails = gotoPostDetails,
)

@Composable
fun Post(
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    lastEditDate: Instant?,
    content: UiPostContent,
    modifier: Modifier = Modifier,
    gotoProfile: (String) -> Unit = {},
    gotoPostDetails: (String) -> Unit = {},
) = Column(modifier) {
    PostHeader(
        modifier = Modifier
            .fillMaxWidth(),
        author = author,
        publishDate = publishDate,
        isRepost = content is UiPostContent.Repost,
        terminateAction =
            if (lastEditDate != null) UiPostTerminateAction.Edited(lastEditDate)
            else null,
    )

    Spacer(Modifier.height(8.dp))

    PostContent(
        modifier = Modifier
            .fillMaxWidth(),
        content = content,
        gotoAuthorProfile = { gotoProfile(author?.idOrNull ?: return@PostContent) },
        gotoProfile = gotoProfile,
        gotoPostDetails = gotoPostDetails,
    )
}
