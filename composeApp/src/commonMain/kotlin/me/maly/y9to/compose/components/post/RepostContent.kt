package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.dropBottom
import me.maly.y9to.compose.utils.flipVertical
import me.maly.y9to.compose.utils.takeBottom
import me.maly.y9to.compose.utils.takeHorizontal
import me.maly.y9to.compose.utils.takeTop
import me.maly.y9to.types.UiPostAction
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview


@Composable
fun RepostContent(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview,
    gotoPostDetails: ((String) -> Unit)? = null,
    gotoAuthorProfile: ((String) -> Unit)? = null,
    contentPadding: ContentPadding = PaddingValues.Zero,
) {
    when (preview) {
        is UiRepostPreview.Post -> RepostContent(modifier, preview, gotoPostDetails, gotoAuthorProfile, contentPadding)
        is UiRepostPreview.DeletedPost -> RepostContent(modifier, preview, gotoAuthorProfile, contentPadding)
    }
}

@Composable
fun RepostContent(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview.Post,
    gotoPostDetails: ((String) -> Unit)? = null,
    gotoAuthorProfile: ((String) -> Unit)? = null,
    contentPadding: ContentPadding = PaddingValues.Zero,
) {
    Column(modifier) {
        PostHeader(
            author = preview.author,
            publishDate = preview.publishDate,
            isRepost = preview.content is UiPostContent.Repost,
            action =
                if (preview.lastEditDate != null) UiPostAction.Edited(preview.lastEditDate)
                else null,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = contentPadding.dropBottom() + contentPadding.takeTop().flipVertical(),
            onClick = gotoAuthorProfile?.let {{
                gotoAuthorProfile(preview.author.idOrNull ?: return@let)
            }},
        )

        PostContent(
            preview.content,
            Modifier.padding(contentPadding.takeHorizontal()),
            gotoPostDetails = gotoPostDetails,
            gotoAuthorProfile = gotoAuthorProfile,
        )

        Spacer(Modifier.padding(contentPadding.takeBottom()))
    }
}

@Composable
fun RepostContent(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview.DeletedPost,
    gotoAuthorProfile: ((String) -> Unit)? = null,
    contentPadding: ContentPadding = PaddingValues.Zero,
) {
    Column(modifier) {
        PostHeader(
            author = preview.author,
            publishDate = preview.deletionDate,
            isRepost = false,
            action = UiPostAction.Deleted(preview.deletionDate),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = contentPadding.dropBottom() + contentPadding.takeTop().flipVertical(),
            onClick = gotoAuthorProfile?.let {{
                gotoAuthorProfile(preview.author.idOrNull ?: return@let)
            }},
        )

        Text(
            modifier = Modifier.padding(contentPadding.takeHorizontal()),
            text = "This post was deleted",
            fontStyle = FontStyle.Italic
        )

        Spacer(Modifier.padding(contentPadding.takeBottom()))
    }
}
