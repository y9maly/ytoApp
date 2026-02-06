package me.maly.y9to.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CircularProgressIndicatorWithIcon(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
    icon: @Composable () -> Unit,
) {
    Box(modifier) {
        CircularProgressIndicator(
            modifier = Modifier.matchParentSize(),
            color = color,
            strokeWidth = strokeWidth,
            trackColor = trackColor,
            strokeCap = strokeCap,
            gapSize = gapSize,
        )

        Box(Modifier.matchParentSize()) {
            icon()
        }
    }
}
