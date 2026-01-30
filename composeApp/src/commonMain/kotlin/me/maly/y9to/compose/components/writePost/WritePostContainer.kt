package me.maly.y9to.compose.components.writePost

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
internal fun WritePostContainer(
    state: WritePostState,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Column {
        AccountHeader(
            state.writeAccount,
            onClick = { state.expanded = !state.expanded }
        )

        Box(Modifier.height(6.dp).fillMaxWidth().background(Color.Black))

        SharedTransitionLayout {
            AnimatedContent(state.expanded, modifier) { expanded ->
                LaunchedEffect(Unit) {
                    if (expanded) focusRequester.requestFocus()
                }

                if (expanded) {
                    ExpandedContent(
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        value = state.text,
                        onValueChange = state::onTextChange,
                        onCollapse = state::onCollapse,
                        onPublish = state::onPublish,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                } else {
                    CollapsedContent(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.text,
                        onValueChange = state::onTextChange,
                        onFocused = state::onFocused,
                        onPublish = state::onPublish,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            }
        }
    }
}
