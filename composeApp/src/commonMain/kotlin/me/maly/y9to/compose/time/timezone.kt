package me.maly.y9to.compose.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant


val LocalTimeZone = staticCompositionLocalOf { TimeZone.currentSystemDefault() }

@Composable
fun Instant.toLocalDateTime(): LocalDateTime {
    val timeZone = LocalTimeZone.current
    return remember(this, timeZone) { this.toLocalDateTime(timeZone) }
}

@Composable
fun LocalDate.toInstantAt(hour: Int, minute: Int, second: Int = 0): Instant {
    val timeZone = LocalTimeZone.current
    return remember(this, timeZone) { this.atTime(hour, minute, second).toInstant(timeZone) }
}

@Composable
fun LocalDateTime.toInstant(): Instant {
    val timeZone = LocalTimeZone.current
    return remember(this, timeZone) { this.toInstant(timeZone) }
}
