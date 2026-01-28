package me.maly.y9to.compose.time

import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.Clock


val LocalClock = staticCompositionLocalOf<Clock> { Clock.System }
