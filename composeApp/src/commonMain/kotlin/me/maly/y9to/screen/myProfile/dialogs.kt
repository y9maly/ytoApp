@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package me.maly.y9to.screen.myProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.maly.y9to.compose.time.LocalClock
import me.maly.y9to.compose.time.toInstantAt
import me.maly.y9to.compose.time.toLocalDateTime
import y9to.common.types.Birthday
import kotlin.time.Instant


@Composable
fun ChangeBirthdayDialog(
    visible: Boolean,
    initial: Birthday?,
    onSave: (Birthday?) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val clock = LocalClock.current
    val currentYear = remember(clock) { clock.now() }.toLocalDateTime().year
    val state = rememberDatePickerState(
        yearRange = 1900..currentYear,
        initialSelectedDateMillis = initial
            ?.toLocalDate(currentYear)
            ?.toInstantAt(0, 0)
            ?.toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                OutlinedButton({
                    onSave(null)
                }) {
                    Text("Delete")
                }

                Spacer(Modifier.weight(1f))

                Button({
                    val selectedDateMillis = state.selectedDateMillis
                        ?: return@Button
                    val selectedDate = Instant.fromEpochMilliseconds(selectedDateMillis)
                        .toLocalDateTime(TimeZone.UTC)
                    val year = selectedDate.year.takeIf { it < currentYear }
                    val month = selectedDate.month
                    val day = selectedDate.day
                    onSave(Birthday(year, month, day))
                }) {
                    Text("Save")
                }
            }
        }
    ) {
        DatePicker(state)
    }
}

@Composable
fun ConfirmLogOutDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Log out")
        },
        text = {
            Text("Are you sure you want to log out?")
        },
        confirmButton = {
            OutlinedButton(
                onClick = onConfirm,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.error,
                )
            ) {
                Text("Log out")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
internal fun LoggingOutDialog(
    visible: Boolean,
) {
    if (!visible) return

    BasicAlertDialog(onDismissRequest = {}) {
        Surface(Modifier.clip(CircleShape)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Logging out...")
            }
        }
    }
}
