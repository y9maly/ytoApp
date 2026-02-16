package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import y9to.libs.stdlib.delegates.static


object TemplateProfileScreenDefaults


val TemplateProfileScreenDefaults.LocalAvatarShape by static {
    staticCompositionLocalOf<Shape> { CircleShape }
}
