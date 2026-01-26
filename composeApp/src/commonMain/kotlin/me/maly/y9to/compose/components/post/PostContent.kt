package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.maly.y9to.types.UiPostContent


@Composable
fun PostContent(
    content: UiPostContent,
    modifier: Modifier = Modifier,
    gotoAuthorProfile: () -> Unit = {},
    gotoProfile: (String) -> Unit = {},
    gotoPostDetails: (String) -> Unit = {},
) {
    when (content) {
        is UiPostContent.Standalone -> PostContent(content, modifier)

        is UiPostContent.Repost -> PostContent(content, modifier,
            gotoOriginalPostDetails = {
                gotoPostDetails(content.originalPreview.idOrNull ?: return@PostContent)
            },
            gotoOriginalPostAuthorProfile = gotoAuthorProfile,
        )
    }
}

@Composable
fun PostContent(
    content: UiPostContent.Standalone,
    modifier: Modifier = Modifier,
) {
    Text(content.text, modifier)
}

@Composable
fun PostContent(
    content: UiPostContent.Repost,
    modifier: Modifier = Modifier,
    gotoOriginalPostAuthorProfile: () -> Unit = {},
    gotoOriginalPostDetails: () -> Unit = {},
) = Column(modifier) {
    val comment = content.comment

    if (comment != null)
        Text(comment)

    Spacer(Modifier.height(6.dp))

    OutlinedCard {
        RepostPreview(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(vertical = 8.dp),
            preview = content.originalPreview,
        )
    }
}
