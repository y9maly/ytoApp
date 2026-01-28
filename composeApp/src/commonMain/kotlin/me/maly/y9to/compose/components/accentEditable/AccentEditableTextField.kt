package me.maly.y9to.compose.components.accentEditable

import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccentEditableTextField(
    name: String,
    value: TextFieldValue,
    onChangeValue: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    editMode: Boolean = false,
    hint: String = "",
    typeViewTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        color = colorScheme.outline,
    ),
    typeEditTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        color = colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium,
    ),
    valueViewTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 18.sp,
        color = colorScheme.onSurfaceVariant,
    ),
    valueEditTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        color = colorScheme.onSurface,
    ),
    hintColor: Color = valueEditTextStyle.color.copy(alpha = valueEditTextStyle.color.alpha / 2),
    lookaheadScope: LookaheadScope? = null,
) {
    BoxWithConstraints(modifier) {
        LookaheadScope {
            val lookaheadScope by rememberUpdatedState(lookaheadScope ?: this)

            val typeTargetTextStyle =
                if (!editMode) typeViewTextStyle
                else typeEditTextStyle
            val typeTextStyle = lerp(
                typeViewTextStyle,
                typeEditTextStyle,
                animateFloatAsState(if (!editMode) 0f else 1f).value
            )
            val typeTargetFontSize = typeTargetTextStyle.fontSize

            val typeFontSizeValue by animateFloatAsState(typeTargetFontSize.value)

            val latestValueViewTextStyle by rememberUpdatedState(valueViewTextStyle)
            val latestEditViewTextStyle by rememberUpdatedState(valueEditTextStyle)
            val latestName by rememberUpdatedState(name)
            val latestTypeTextStyle by rememberUpdatedState(typeTextStyle)
            val latestTypeTargetFontSize by rememberUpdatedState(typeTargetFontSize)
            val latestValue by rememberUpdatedState(value)
            val latestEditMode by rememberUpdatedState(editMode)
            val latestOnChangeValue by rememberUpdatedState(onChangeValue)
            val latestHint by rememberUpdatedState(hint)
            val latestHintColor by rememberUpdatedState(hintColor)

            val typePlaceable = remember { movableContentOf {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateBounds(lookaheadScope),
                    text = latestName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = latestTypeTextStyle,
                    fontSize = TextUnit(typeFontSizeValue, latestTypeTargetFontSize.type)
                )
            } }

            val valuePlaceable = remember { movableContentOf {
                val textStyle = lerp(
                    latestValueViewTextStyle,
                    latestEditViewTextStyle,
                    animateFloatAsState(if (latestEditMode) 1f else 0f).value
                )

                BasicTextField(
                    value = latestValue,
                    readOnly = !latestEditMode,
                    onValueChange = latestOnChangeValue,
                    textStyle = textStyle,
                    modifier = Modifier.fillMaxWidth().animateBounds(lookaheadScope),
                    decorationBox = {
                        if (latestValue.text.isEmpty())
                            Text(
                                latestHint,
                                style = latestEditViewTextStyle.copy(color = latestHintColor)
                            )
                        it()
                    }
                )
            } }

            val shape = remember { RoundedCornerShape(8.dp) }
            val alpha by animateFloatAsState(if (!editMode) 0f else 1f)
            val backgroundColor = colorScheme.surfaceContainer.copy(alpha = alpha)
            val borderColor = colorScheme.outline.copy(alpha = alpha)

            Box(Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .animateBounds(lookaheadScope)
            ) {
                if (!editMode) {
                    Column(Modifier.padding(horizontal = 12.dp)) {
                        valuePlaceable()
                        Spacer(Modifier.height(5.dp))
                        typePlaceable()
                    }
                } else {
                    Column {
                        Box(Modifier.padding(horizontal = 12.dp).padding(top = 12.dp)) {
                            typePlaceable()
                        }
                        Box(Modifier.padding(start = 12.dp).padding(vertical = 12.dp)) {
                            valuePlaceable()
                        }
                    }
                }
            }
        }
    }
}
