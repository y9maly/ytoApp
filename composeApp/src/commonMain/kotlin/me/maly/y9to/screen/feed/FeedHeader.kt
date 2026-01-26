package me.maly.y9to.screen.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.search


@Composable
fun FeedHeader(
    state: FeedHeaderState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    Column(modifier.padding(contentPadding)) {
        SmallSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {},
            query = ""
        )

        Spacer(Modifier.height(24.dp))

        when (state) {
            is FeedHeaderState.Loading -> {
                Text(
                    "Hello",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            is FeedHeaderState.Authenticated -> {
                Text(
                    "Hello, ${state.firstName} ${state.lastName ?: ""}!",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            is FeedHeaderState.Unauthenticated -> {
                Text(
                    "Hello, Guest",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            is FeedHeaderState.Error -> {
                Text(
                    "Oops, something went wrong...",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SmallSearchBar(
    query: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Row(modifier
        .clip(RoundedCornerShape(100))
        .clickable(remember { MutableInteractionSource() }, ripple()) { onClick() }
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .padding(vertical = 6.dp, horizontal = 16.dp)
        .semantics {
            contentDescription = "Search bar"
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Search",
            Modifier.weight(1f),
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            painterResource(Res.drawable.search),
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(CircleShape)
                .size(24.dp)
        )
    }
}
