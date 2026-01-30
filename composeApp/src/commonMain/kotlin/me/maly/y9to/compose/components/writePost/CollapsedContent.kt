package me.maly.y9to.compose.components.writePost

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.attach_file


@Composable
internal fun CollapsedContent(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocused: () -> Unit,
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
            EnterExitState.PostExit -> 0.dp
            EnterExitState.Visible,
            EnterExitState.PreEnter -> 12.dp
        }
    }

    Column(modifier.padding(16.dp)) {
        Row {
            BasicTextField(
                modifier = Modifier
                    .onFocusChanged { if (it.isFocused) onFocused() }
                    .weight(1f)
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
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                value = value,
                onValueChange = onValueChange,
                cursorBrush = SolidColor(Color.Gray),
                textStyle = textStyle.copy(color = Color.White),
            ) { text ->
                Row(verticalAlignment = CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "attach"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        painter = painterResource(Res.drawable.attach_file),
                        contentDescription = null,
                        tint = Color.Gray
                    )

                    Spacer(Modifier.width(8.dp))

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
            }
        }

        Spacer(Modifier.height(8.dp))

        PublishButton(onPublish, enabled = value.text.isNotBlank(), Modifier
            .height(26.dp)
            .align(Alignment.End)
            .sharedElement(
                rememberSharedContentState(key = "button"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
    }
}
