package me.maly.y9to.compose.components.writePost

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_circle_up


@Composable
fun PublishButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier
        .clip(CircleShape)
        .background(if (enabled) Color(0xff5553DF) else Color(0xff686868))
        .clickable(enabled) { onClick() }
        .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = CenterVertically,
    ) {
        Text("Publish", style = typography.labelMedium)
        Spacer(Modifier.width(4.dp))
        Icon(painterResource(Res.drawable.arrow_circle_up), null, tint = Color.White)
    }
}