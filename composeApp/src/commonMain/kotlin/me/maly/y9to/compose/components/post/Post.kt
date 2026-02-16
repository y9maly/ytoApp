package me.maly.y9to.compose.components.post

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.NoHoverInteractionSource
import me.maly.y9to.compose.utils.dropBottom
import me.maly.y9to.compose.utils.flipVertical
import me.maly.y9to.compose.utils.takeBottom
import me.maly.y9to.compose.utils.takeHorizontal
import me.maly.y9to.compose.utils.takeTop
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAction
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import kotlin.time.Instant


@Composable
fun PostCard(
    post: UiPost,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    gotoPostDetails: ((postId: String) -> Unit)? = null,
    gotoAuthorProfile: ((authorId: String) -> Unit)? = null,
    overlay: @Composable BoxScope.() -> Unit = {},
) = PostCard(
    author = post.author,
    publishDate = post.publishDate,
    lastEditDate = post.lastEditDate,
    content = post.content,
    modifier = modifier,
    menu = menu,
    onClick = onClick,
    gotoPostDetails = gotoPostDetails,
    gotoAuthorProfile = gotoAuthorProfile,
    overlay = overlay,
)

/**
 * @param overlay for example:
 * ```kotlin
 * Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f))) {
 *     LoadingIndicator(Modifier.align(Center))
 * }
 * ```
 */
@Composable
fun PostCard(
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    lastEditDate: Instant?,
    content: UiPostContent,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    gotoPostDetails: ((postId: String) -> Unit)? = null,
    gotoAuthorProfile: ((authorId: String) -> Unit)? = null,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Box {
            Post(
                author = author,
                publishDate = publishDate,
                lastEditDate = lastEditDate,
                content = content,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                menu = menu,
                gotoPostDetails = gotoPostDetails,
                gotoAuthorProfile = gotoAuthorProfile,
            )

            overlay()
        }
    }

    val elevation = CardDefaults.outlinedCardElevation(
        defaultElevation = 0.dp,
        hoveredElevation = 0.dp,
        pressedElevation = 0.dp,
        focusedElevation = 0.dp
    )

    if (onClick == null) {
        OutlinedCard(
            modifier = modifier,
            elevation = elevation,
            content = cardContent,
        )
        return
    }

    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
        interactionSource = remember { NoHoverInteractionSource() },
        elevation = elevation,
        content = cardContent,
    )
}


@Composable
fun Post(
    post: UiPost,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
    gotoPostDetails: ((postId: String) -> Unit)? = null,
    gotoAuthorProfile: ((authorId: String) -> Unit)? = null,
    contentPadding: ContentPadding = ContentPadding.Zero,
) = Post(
    author = post.author,
    publishDate = post.publishDate,
    lastEditDate = post.lastEditDate,
    content = post.content,
    modifier = modifier,
    menu = menu,
    gotoPostDetails = gotoPostDetails,
    gotoAuthorProfile = gotoAuthorProfile,
    contentPadding = contentPadding,
)

@Composable
fun Post(
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    lastEditDate: Instant?,
    content: UiPostContent,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
    gotoPostDetails: ((postId: String) -> Unit)? = null,
    gotoAuthorProfile: ((authorId: String) -> Unit)? = null,
    contentPadding: ContentPadding = ContentPadding.Zero,
) = Column(modifier) {
    PostHeader(
        modifier = Modifier
            .fillMaxWidth(),
        author = author,
        publishDate = publishDate,
        isRepost = content is UiPostContent.Repost,
        action =
            if (lastEditDate != null) UiPostAction.Edited(lastEditDate)
            else null,
        menu = menu,
        contentPadding = contentPadding.dropBottom() + contentPadding.takeTop().flipVertical(),
        onClick = gotoAuthorProfile?.let {{
            gotoAuthorProfile(author?.idOrNull ?: return@let)
        }},
    )

    PostContent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding.takeHorizontal()),
        content = content,
        gotoPostDetails = gotoPostDetails,
        gotoAuthorProfile = gotoAuthorProfile,
    )

    Spacer(Modifier.padding(contentPadding.takeBottom()))
}
