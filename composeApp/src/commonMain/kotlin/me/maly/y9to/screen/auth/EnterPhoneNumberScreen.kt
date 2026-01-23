package me.maly.y9to.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterPhoneNumberScreen(
    phoneNumber: String,
    onChangePhoneNumber: (String) -> Unit,
    onEmit: () -> Boolean,
    loading: Boolean,
    invalidPhoneNumber: Boolean,
    modifier: Modifier = Modifier,
) {
    var confirmDialog by remember { mutableStateOf(false) }

    val backgroundBlur by animateDpAsState(
        if (confirmDialog) 12.dp
        else if (loading) 8.dp
        else 0.dp
    )
    val fabEnabled = !invalidPhoneNumber
    val fabAlpha by animateFloatAsState(if (fabEnabled) 1f else 0f)

    Scaffold(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .blur(backgroundBlur)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Your phone number",
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(Modifier.height(48.dp))

            Text(
                "You can use any sequence of numbers. It will be public.",
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(48.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phoneNumber,
                onValueChange = {
                    onChangePhoneNumber(it.filter { it.isDigit() })
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                label = { Text("Phone number") },
                prefix = { Text("+") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(Modifier.height(12.dp))

            AnimatedVisibility(invalidPhoneNumber) {
                Text("This phone number is invalid", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(48.dp))

            FloatingActionButton(
                onClick = { if (fabEnabled) confirmDialog = true },
                Modifier
                    .align(Alignment.End)
                    .alpha(fabAlpha),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
            ) {
//                Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, null)
            }
        }

        if (confirmDialog) {
            AlertDialog(
                onDismissRequest = { confirmDialog = false },
                confirmButton = {
                    Button(onClick = {
                        confirmDialog = false
                        onEmit()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    OutlinedButton({ confirmDialog = false }) {
                        Text("No, change the number", Modifier.padding(horizontal = 16.dp))
                    }
                },
                title = {
                    Text("Is this the correct number?")
                },
                text = {
                    Text(phoneNumber, style = MaterialTheme.typography.bodyLarge)
                }
            )
        }

        if (loading) {
            BasicAlertDialog(
                onDismissRequest = { /* do nothing */ },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(24.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
