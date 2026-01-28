package me.maly.y9to.compose.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant


val LocalTimeZone = staticCompositionLocalOf { TimeZone.currentSystemDefault() }

@Composable
fun Instant.toLocalDateTime() = toLocalDateTime(LocalTimeZone.current)

@Composable
fun Instant.rememberLocalDateTime(): LocalDateTime {
    val timeZone = LocalTimeZone.current
    return remember(this, timeZone) { this.toLocalDateTime(timeZone) }
}
