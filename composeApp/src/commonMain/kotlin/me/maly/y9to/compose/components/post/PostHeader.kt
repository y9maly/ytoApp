package me.maly.y9to.compose.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.repeat
import kotlin.time.Instant


@Composable
fun PostHeader(
    author: UiPostAuthorPreview,
    publishDate: Instant,
    isRepost: Boolean,
    isEdited: Boolean,
    isDeleted: Boolean,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier
        .height(IntrinsicSize.Min),
    verticalAlignment = Alignment.CenterVertically
) {
    if (isRepost) {
        Icon(painterResource(Res.drawable.repeat), null, Modifier.size(24.dp))
        Spacer(Modifier.width(4.dp))
    }

    Spacer(Modifier.width(8.dp))

    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) {
        PostAuthorPreview(author)

        val text = publishDate.toLocalDateTime(TimeZone.currentSystemDefault()).run {
            buildString {
                if (isDeleted) {
                    append("Deleted at ")
                }

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

                if (isEdited)
                    append(" (edited)")
            }
        }

        Text(text)
    }
}
