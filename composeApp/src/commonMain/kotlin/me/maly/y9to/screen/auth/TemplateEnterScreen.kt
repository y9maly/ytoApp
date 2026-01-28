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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import me.maly.y9to.screen.auth.TemplateEnterScreen.EmitResult
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_forward_ios


object TemplateEnterScreen {
    data class DialogProperties(
        val title: String,
        val message: String,
        val confirm: String,
        val dismiss: String,
    )

    sealed interface EmitResult {
        data object Ok : EmitResult
        data class ShowDialog(val properties: DialogProperties) : EmitResult
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEnterScreen(
    value: TextFieldValue,
    onChangeValue: (TextFieldValue) -> Unit,
    onEmit: (confirmed: Boolean) -> EmitResult,
    title: String,
    subtitle: String,
    subsubtitle: String?,
    prefix: (@Composable () -> Unit)?,
    label: (@Composable () -> Unit)?,
    loading: Boolean,
    errorText: String?,
    fabEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var dialog by remember { mutableStateOf<TemplateEnterScreen.DialogProperties?>(null) }

    val backgroundBlur by animateDpAsState(
        if (dialog != null) 12.dp
        else if (loading) 6.dp
        else 0.dp
    )
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
                title,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(Modifier.height(48.dp))

            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall
            )

            if (subsubtitle != null) {
                Spacer(Modifier.height(16.dp))
                Text(subsubtitle, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(48.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onChangeValue,
                textStyle = MaterialTheme.typography.bodyLarge,
                label = label,
                prefix = prefix,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(Modifier.height(12.dp))

            AnimatedVisibility(errorText != null) {
                var lastErrorText by remember { mutableStateOf(errorText) }
                lastErrorText = errorText ?: lastErrorText
                Text(lastErrorText ?: return@AnimatedVisibility, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(48.dp))

            FloatingActionButton(
                onClick = {
                    if (!fabEnabled) return@FloatingActionButton
                    when (val result = onEmit(false)) {
                        EmitResult.Ok -> { /* do nothing */ }
                        is EmitResult.ShowDialog -> dialog = result.properties
                    }
                },
                Modifier
                    .align(Alignment.End)
                    .alpha(fabAlpha),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
            ) {
                Icon(painterResource(Res.drawable.arrow_forward_ios), null)
            }
        }

        val currentDialog = dialog
        if (currentDialog != null) {
            AlertDialog(
                onDismissRequest = { dialog = null },
                confirmButton = {
                    Button(onClick = {
                        dialog = null
                        onEmit(true)
                    }) {
                        Text(currentDialog.confirm)
                    }
                },
                dismissButton = {
                    OutlinedButton({ dialog = null }) {
                        Text(currentDialog.dismiss, Modifier.padding(horizontal = 16.dp))
                    }
                },
                title = {
                    Text(currentDialog.title)
                },
                text = {
                    Text(currentDialog.message, style = MaterialTheme.typography.bodyLarge)
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
