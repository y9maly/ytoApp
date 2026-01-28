package me.maly.y9to.compose.components.accentEditable

import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AccentEditableOption(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
    lookaheadScope: LookaheadScope? = null,
    leading: (@Composable LookaheadScope.() -> Unit)? = null,
    trailing: (@Composable LookaheadScope.() -> Unit)? = null,
    editMode: Boolean = false
) {
    val editMode by rememberUpdatedState(editMode)
    val name by rememberUpdatedState(name)
    val value by rememberUpdatedState(value)

    LookaheadScope {
        val lookaheadScope = lookaheadScope ?: this
        val latestLookaheadScope by rememberUpdatedState(lookaheadScope)

        Row(modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
            Box(Modifier.align(Alignment.CenterVertically)) {
                leading?.invoke(lookaheadScope)
            }

            Column(Modifier.weight(1f)) {
                val nameContent = remember {
                    movableContentOf {
                        val size by animateFloatAsState(
                            if (editMode) 18f else 14f
                        )

                        val color by animateColorAsState(
                            if (editMode)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.outline
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateBounds(latestLookaheadScope),
                            text = name,
                            color = color,
                            fontSize = size.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                val valueContent = remember {
                    movableContentOf {
                        val size by animateFloatAsState(
                            if (editMode) 14f else 18f
                        )

                        val color by animateColorAsState(
                            if (editMode)
                                MaterialTheme.colorScheme.outline
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateBounds(latestLookaheadScope),
                            text = value,
                            color = color,
                            fontSize = size.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                if (editMode) {
                    nameContent()
                    Spacer(
                        modifier = Modifier
                            .height(6.dp)
                            .animateBounds(lookaheadScope)
                    )
                    valueContent()
                } else {
                    valueContent()
                    Spacer(
                        modifier = Modifier
                            .height(6.dp)
                            .animateBounds(lookaheadScope)
                    )
                    nameContent()
                }
            }

            Box(Modifier.align(Alignment.CenterVertically)) {
                trailing?.invoke(lookaheadScope)
            }
        }
    }
}
