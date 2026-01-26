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
import me.maly.y9to.types.UiPostContent


@Composable
fun PostCard(
    post: UiPost,
    modifier: Modifier = Modifier,
    gotoProfile: (String) -> Unit = {},
    gotoPostDetails: (String) -> Unit = {},
) {
    OutlinedCard(modifier) {
        Post(
            post = post,
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
) = Column(modifier) {
    PostHeader(
        modifier = Modifier
            .fillMaxWidth(),
        author = post.author,
        publishDate = post.publishDate,
        isRepost = post.content is UiPostContent.Repost,
        isDeleted = false,
        isEdited = post.lastEditDate != null,
    )

    Spacer(Modifier.height(8.dp))

    PostContent(
        modifier = Modifier
            .fillMaxWidth(),
        content = post.content,
        gotoAuthorProfile = { gotoProfile(post.author.idOrNull ?: return@PostContent) },
        gotoProfile = gotoProfile,
        gotoPostDetails = gotoPostDetails,
    )
}
