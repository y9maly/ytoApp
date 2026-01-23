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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties


@Composable
fun EnterPhoneNumberScreen(
    phoneNumber: TextFieldValue,
    onChangePhoneNumber: (TextFieldValue) -> Unit,
    onEmit: () -> Unit,
    loading: Boolean,
    invalidPhoneNumber: Boolean,
    modifier: Modifier = Modifier,
) {
    BaseEnterScreen(
        modifier = modifier,
        value = phoneNumber,
        onChangeValue = { onChangePhoneNumber(it.copy(text = it.text.filter { it.isDigit() })) },
        title = "Your phone number",
        subtitle = "You can use any sequence of numbers. It will be public.",
        subsubtitle = null,
        label = { Text("Phone number") },
        prefix = { Text("+") },
        loading = loading,
        errorText = "This phone number is invalid".takeIf { invalidPhoneNumber },
        fabEnabled = !invalidPhoneNumber,
        onEmit = onEmit@{ confirmed ->
            if (!confirmed)
                return@onEmit BaseEnterScreen.EmitResult.ShowDialog(BaseEnterScreen.DialogProperties(
                    title = "Is this the correct number?",
                    message = phoneNumber.text,
                    confirm = "Yes",
                    dismiss = "No, change the number"
                ))
            onEmit()
            BaseEnterScreen.EmitResult.Ok
        },
    )
}
