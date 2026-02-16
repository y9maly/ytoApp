package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.maly.y9to.compose.utils.NoHoverInteractionSource
import me.maly.y9to.types.UiPostContent


@Composable
fun PostContent(
    content: UiPostContent,
    modifier: Modifier = Modifier,
    gotoPostDetails: ((String) -> Unit)? = null,
    gotoAuthorProfile: ((String) -> Unit)? = null,
) {
    val gotoPostDetails by rememberUpdatedState(gotoPostDetails)
    val gotoAuthorProfile by rememberUpdatedState(gotoAuthorProfile)

    when (content) {
        is UiPostContent.Standalone -> PostContent(content, modifier)

        is UiPostContent.Repost -> PostContent(content, modifier,
            gotoPostDetails = gotoPostDetails,
            gotoAuthorProfile = gotoAuthorProfile,
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
    gotoPostDetails: ((String) -> Unit)? = null,
    gotoAuthorProfile: ((String) -> Unit)? = null,
) = Column(modifier) {
    val comment = content.comment

    if (comment != null)
        Text(comment)

    Spacer(Modifier.height(6.dp))

    OutlinedCard(
        interactionSource = remember { NoHoverInteractionSource() },
        onClick = {
            val id = content.originalPreview.idOrNull ?: return@OutlinedCard
            gotoPostDetails?.invoke(id)
        },
    ) {
        RepostContent(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            preview = content.originalPreview,
            gotoPostDetails = gotoPostDetails,
            gotoAuthorProfile = gotoAuthorProfile,
        )
    }
}
