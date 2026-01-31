@file:OptIn(ExperimentalMaterial3Api::class)

package me.maly.y9to.screen.myProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest


@Composable
fun ChangeFullNameBottomSheet(
    visible: Boolean,
    initialFirstName: String,
    initialLastName: String?,
    onSave: (String, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberModalBottomSheetState()

    LaunchedEffect(visible) {
        if (visible && state.targetValue == SheetValue.Hidden) {
            state.show()
        } else if (!visible && state.targetValue != SheetValue.Hidden) {
            state.hide()
        }
    }

    if (!visible && !state.isVisible && !state.isAnimationRunning)
        return

    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismiss,
    ) {
        var firstName by remember { mutableStateOf(initialFirstName) }
        var lastName by remember { mutableStateOf(initialLastName ?: "") }
        val canSave = firstName.isNotBlank()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Your name",
                style = typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onSave(firstName.trim(), lastName.trim().takeIf { it.isNotBlank() }) },
                    enabled = canSave
                ) {
                    Text("Save")
                }
            }
        }
    }
}
