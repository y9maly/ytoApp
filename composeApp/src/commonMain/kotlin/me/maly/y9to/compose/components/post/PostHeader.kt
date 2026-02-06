package me.maly.y9to.compose.components.post

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDateTime
import me.maly.y9to.compose.time.toLocalDateTime
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostTerminateAction
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.cat1
import y9to.composeapp.generated.resources.deletedUser
import y9to.composeapp.generated.resources.repeat
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostHeader(
    author: UiPostAuthorPreview?,
    publishDate: Instant,
    isRepost: Boolean,
    terminateAction: UiPostTerminateAction?,
    modifier: Modifier = Modifier,
    menu: @Composable () -> Unit = {},
) = Row(
    modifier = modifier
        .height(IntrinsicSize.Min),
    verticalAlignment = Alignment.CenterVertically
) {
    val publishLocalDate = publishDate.toLocalDateTime()

    val avatar = when (author) {
        null -> null
        is UiPostAuthorPreview.User -> painterResource(Res.drawable.cat1)
        is UiPostAuthorPreview.DeletedUser -> painterResource(Res.drawable.deletedUser)
    }

    AnimatedContent(avatar) { avatar ->
        if (avatar == null) {
            LoadingIndicator(Modifier.size(42.dp))
            return@AnimatedContent
        }

        Image(
            avatar,
            if (author == null) "Avatar of unknown user"
            else "Avatar of user ${author.displayName}",
            Modifier.size(42.dp).clip(CircleShape)
        )
    }

    Spacer(Modifier.width(8.dp))

    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
        Row {
            if (isRepost) {
                Icon(painterResource(Res.drawable.repeat), null, Modifier.size(24.dp))
                Spacer(Modifier.width(4.dp))
            }

            when (author) {
                is UiPostAuthorPreview.User, null -> {
                    Text(author?.displayName ?: "...", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                }

                is UiPostAuthorPreview.DeletedUser -> {
                    Text(author.displayName, fontStyle = FontStyle.Italic, fontSize = 16.sp)
                }
            }

            Text(" • ${publishLocalDate.formatToString()}", fontSize = 14.sp)

            Spacer(Modifier.weight(1f))

            menu()
        }

        when (terminateAction) {
            null -> {}

            is UiPostTerminateAction.Deletion -> {
                val deletionLocalDate = terminateAction.timestamp.toLocalDateTime()

                Text("Deleted ${deletionLocalDate.formatToString()}", fontSize = 12.sp)
            }

            is UiPostTerminateAction.Edited -> {
                val lastEditLocalDate = terminateAction.timestamp.toLocalDateTime()

                Text("Edited ${lastEditLocalDate.formatToString()}", fontSize = 12.sp)
            }
        }
    }
}

private fun LocalDateTime.formatToString() = buildString {
    append(day)
    append(" ")
    append(month.name.lowercase())
    append(" at ")
    if (hour <= 9)
        append(0)
    append(hour)
    append(":")
    if (minute <= 9)
        append(0)
    append(minute)
}
