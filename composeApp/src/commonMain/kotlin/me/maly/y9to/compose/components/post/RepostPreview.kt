package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview


@Composable
fun RepostPreview(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview,
) {
    when (preview) {
        is UiRepostPreview.Post -> RepostPreview(modifier, preview)
        is UiRepostPreview.DeletedPost -> RepostPreview(modifier, preview)
    }
}

@Composable
fun RepostPreview(
    modifier: Modifier = Modifier,
    preview: UiRepostPreview.Post,
) {
    Column(modifier) {
        PostHeader(
            author = preview.author,
            publishDate = preview.publishDate,
            isRepost = preview.content is UiPostContent.Repost,
            isEdited = preview.lastEditDate != null,
            isDeleted = false,
            Modifier.fillMaxWidth()
        )
        
        PostContent(preview.content)
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
            isEdited = false,
            isDeleted = true,
            Modifier.fillMaxWidth()
        )

        Text("* Original post was deleted *", fontFamily = FontFamily.Cursive)
    }
}
