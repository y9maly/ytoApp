package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp


internal val coverDefaultHeight = 180.dp
internal val avatarSize = 130.dp
internal val avatarBorderWidth = 5.dp
//private val avatarCornerRadius = 16.dp
//private val avatarShape = RoundedCornerShape(avatarCornerRadius - avatarBorderWidth)
//private val avatarBorderShape = RoundedCornerShape(avatarCornerRadius)
internal val avatarShape = CircleShape
internal val avatarBorderShape = CircleShape
internal val avatarAlignment = Alignment { size, space, _ ->
    IntOffset(
        x = (space.width / 2) - (size.width / 2),
        y = space.height - (size.height / 2)
    )
}

internal val collapsedCoverHeight = 90.dp
internal val collapsedAvatarSize = 70.dp
internal val collapsedAvatarAlignment = Alignment { size, space, _ ->
    IntOffset(
        x = 0,
        y = (space.height / 2) - (size.height / 2)
    )
}
