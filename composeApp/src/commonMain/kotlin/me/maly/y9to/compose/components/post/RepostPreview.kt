package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiPostTerminateAction
import me.maly.y9to.types.UiRepostPreview
import kotlin.time.Clock


@Composable
fun RepostPreview(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview,
    gotoPostDetails: ((String) -> Unit)? = null,
) {
    when (preview) {
        is UiRepostPreview.Post -> RepostPreview(modifier, preview, gotoPostDetails)
        is UiRepostPreview.DeletedPost -> RepostPreview(modifier, preview)
    }
}

@Composable
fun RepostPreview(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview.Post,
    gotoPostDetails: ((String) -> Unit)? = null,
) {
    Column(modifier) {
        PostHeader(
            author = preview.author,
            publishDate = preview.publishDate,
            isRepost = preview.content is UiPostContent.Repost,
            terminateAction =
                if (preview.lastEditDate != null) UiPostTerminateAction.Edited(preview.lastEditDate)
                else null,
            Modifier.fillMaxWidth()
        )
        
        PostContent(preview.content, gotoPostDetails = gotoPostDetails)
    }
}

@Composable
fun RepostPreview(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview.DeletedPost,
) {
    Column(modifier) {
        PostHeader(
            author = preview.author,
            publishDate = preview.deletionDate,
            isRepost = false,
            terminateAction = UiPostTerminateAction.Deletion(preview.deletionDate),
            Modifier.fillMaxWidth()
        )

        Text("This post was deleted", fontStyle = FontStyle.Italic)
    }
}
