package me.maly.y9to.compose.components.writePost

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.attach_file
import y9to.composeapp.generated.resources.collapse_content


@Composable
internal fun ExpandedContent(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onCollapse: () -> Unit,
    onPublish: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) = with(sharedTransitionScope) {
    val localTextStyle = LocalTextStyle.current
    val textStyle = remember(localTextStyle) {
        localTextStyle.copy(fontSize = 14.sp, color = Color.White)
    }

    val topCorners by animatedVisibilityScope.transition.animateDp {
        when (it) {
            EnterExitState.PostExit -> 12.dp
            EnterExitState.Visible,
            EnterExitState.PreEnter -> 0.dp
        }
    }

    Column(modifier
        .sharedBounds(
            rememberSharedContentState(key = "bounds"),
            animatedVisibilityScope = animatedVisibilityScope,
            enter = fadeIn(),
            exit = fadeOut(),
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
        )
        .clip(RoundedCornerShape(
            topStart = topCorners,
            topEnd = topCorners,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        ))
        .background(Color(0xff25263B))
        .padding(all = 16.dp)
    ) {
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle.copy(color = Color.White),
            cursorBrush = SolidColor(Color.Gray),
            minLines = 2,
        ) { text ->
            Box(Modifier
                .sharedElement(
                    rememberSharedContentState(key = "text"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            ) {
                if (value.text.isEmpty())
                    Text("What do you think?", style = textStyle, color = Color.Gray)
                text()
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = CenterVertically) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape).clickable {  }.padding(4.dp)
                    .sharedElement(
                        rememberSharedContentState(key = "attach"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                painter = painterResource(Res.drawable.attach_file),
                contentDescription = null,
                tint = Color.Gray
            )

            Spacer(Modifier.width(8.dp))

            Icon(
                modifier = Modifier.clip(CircleShape).clickable { onCollapse() }.padding(4.dp),
                painter = painterResource(Res.drawable.collapse_content),
                contentDescription = null,
                tint = Color.Gray
            )

            Spacer(Modifier.weight(1f))

            PublishButton(onPublish, enabled = value.text.isNotBlank(), Modifier
                .padding(horizontal = 2.dp)
                .height(36.dp)
                .sharedElement(
                    rememberSharedContentState(key = "button"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }
    }
}
