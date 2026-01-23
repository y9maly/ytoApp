package me.maly.y9to.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import pro.respawn.flowmvi.compose.dsl.subscribe
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.reduce


val store = store<AuthScreenState, AuthScreenIntent, AuthScreenAction>(
    AuthScreenState.Unauthenticated(
        phoneNumberAvailable = true,
        emailAvailable = true,
        usernameAvailable = true,
        phoneNumber = "",
        email = "",
        username = "",
    )
) {
    reduce { intent ->
        when (intent) {
            is AuthScreenIntent.ChangeEmail -> updateStateImmediate<AuthScreenState.Unauthenticated, _> {
                action(AuthScreenAction.ShowDialog("Change email from $email to ${intent.email}"))
                copy(email = intent.email)
            }

            is AuthScreenIntent.ChaneUsername -> updateStateImmediate<AuthScreenState.Unauthenticated, _> {
                copy(username = intent.username)
            }

            is AuthScreenIntent.ChanePhoneNumber -> updateStateImmediate<AuthScreenState.Unauthenticated, _> {
                copy(phoneNumber = intent.phoneNumber)
            }

            is AuthScreenIntent.ChaneConfirmCode -> updateStateImmediate<AuthScreenState.ConfirmCode, _> {
                copy(code = intent.code)
            }

            is AuthScreenIntent.ChanePassword -> updateStateImmediate<AuthScreenState.Password, _> {
                copy(password = intent.password)
            }

            AuthScreenIntent.EmitEmail -> updateState<AuthScreenState.Unauthenticated, _> {
                AuthScreenState.ConfirmCode(
                    code = "",
                    source = ConfirmCodeSource.Email,
                    length = 6,
                )
            }

            AuthScreenIntent.EmitUsername -> updateState<AuthScreenState.Unauthenticated, _> {
                AuthScreenState.ConfirmCode(
                    code = "",
                    source = ConfirmCodeSource.entries.random(),
                    length = 4,
                )
            }

            AuthScreenIntent.EmitPhoneNumber -> updateState<AuthScreenState.Unauthenticated, _> {
                AuthScreenState.ConfirmCode(
                    code = "",
                    source = ConfirmCodeSource.PhoneNumber,
                    length = 6,
                )
            }

            AuthScreenIntent.EmitConfirmCode -> updateState<AuthScreenState.ConfirmCode, _> {
                AuthScreenState.Password(
                    password = "",
                    hint = "$code is valid!",
                )
            }

            AuthScreenIntent.EmitPassword -> updateState<AuthScreenState.Password, _> {
                action(AuthScreenAction.ShowDialog("Password $password"))
                AuthScreenState.Authorized(
                    firstName = "First name",
                    lastName = password,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    LaunchedEffect(Unit) { store.start(this).awaitUntilClosed() }

    var nextAlertDialogId by remember { mutableStateOf(0) }
    val alertDialogs = remember { mutableStateMapOf<Int, String>() }
    val state by store.subscribe { action ->
        when (action) {
            is AuthScreenAction.ShowDialog -> alertDialogs[nextAlertDialogId++] = action.text
        }
    }

    alertDialogs.forEach { (id, content) ->
        key(id) {
            BasicAlertDialog({ alertDialogs.remove(id) }) {
                Text(content)
            }
        }
    }

    AnimatedContent(state, contentKey = { it::class }) { state ->
        when (val state = state) {
            is AuthScreenState.Unauthenticated -> {
                UnauthenticatedContent(
                    state = state,
                    onIntent = store::intent
                )
            }

            is AuthScreenState.ConfirmCode -> {
                ConfirmCodeContent(
                    state = state,
                    onIntent = store::intent
                )
            }

            is AuthScreenState.Password -> {
                PasswordContent(
                    state = state,
                    onIntent = store::intent
                )
            }

            is AuthScreenState.Authorized -> {
                AuthorizedContent(state = state)
            }
        }
    }
}

private enum class AuthMethod { Phone, Email, Username }

@Composable
private fun UnauthenticatedContent(
    state: AuthScreenState.Unauthenticated,
    onIntent: (AuthScreenIntent) -> Unit
) {
    val availableMethods = remember(state) {
        buildList {
            if (state.phoneNumberAvailable) add(AuthMethod.Phone)
            if (state.emailAvailable) add(AuthMethod.Email)
            if (state.usernameAvailable) add(AuthMethod.Username)
        }
    }

    if (availableMethods.isEmpty()) return

    var selectedMethod by remember {
        mutableStateOf(availableMethods.first())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (availableMethods.size > 1) {
            SecondaryTabRow(availableMethods.indexOf(selectedMethod)) {
                availableMethods.forEach { method ->
                    Tab(
                        selected = method == selectedMethod,
                        onClick = { selectedMethod = method },
                        text = {
                            Text(
                                text = when (method) {
                                    AuthMethod.Phone -> "Phone"
                                    AuthMethod.Email -> "Email"
                                    AuthMethod.Username -> "Username"
                                }
                            )
                        }
                    )
                }
            }
        }

        when (selectedMethod) {
            AuthMethod.Phone -> {
                AuthInputBlock(
                    value = state.phoneNumber,
                    label = "Phone number",
                    onValueChange = {
                        onIntent(AuthScreenIntent.ChanePhoneNumber(it))
                    },
                    onSubmit = {
                        onIntent(AuthScreenIntent.EmitPhoneNumber)
                    }
                )
            }

            AuthMethod.Email -> {
                AuthInputBlock(
                    value = state.email,
                    label = "Email",
                    onValueChange = {
                        onIntent(AuthScreenIntent.ChangeEmail(it))
                    },
                    onSubmit = {
                        onIntent(AuthScreenIntent.EmitEmail)
                    }
                )
            }

            AuthMethod.Username -> {
                AuthInputBlock(
                    value = state.username,
                    label = "Username",
                    onValueChange = {
                        onIntent(AuthScreenIntent.ChaneUsername(it))
                    },
                    onSubmit = {
                        onIntent(AuthScreenIntent.EmitUsername)
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthInputBlock(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSubmit,
            enabled = value.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun ConfirmCodeContent(
    state: AuthScreenState.ConfirmCode,
    onIntent: (AuthScreenIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = when (state.source) {
                ConfirmCodeSource.PhoneNumber -> "Enter code from SMS"
                ConfirmCodeSource.Email -> "Enter code from email"
            },
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = state.code,
            onValueChange = {
                if (it.length <= state.length) {
                    onIntent(AuthScreenIntent.ChaneConfirmCode(it))
                }
            },
            label = { Text("Confirmation code") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onIntent(AuthScreenIntent.EmitConfirmCode) },
            enabled = state.code.length == state.length,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm")
        }
    }
}

@Composable
private fun PasswordContent(
    state: AuthScreenState.Password,
    onIntent: (AuthScreenIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                onIntent(AuthScreenIntent.ChanePassword(it))
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        state.hint?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = { onIntent(AuthScreenIntent.EmitPassword) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

@Composable
private fun AuthorizedContent(
    state: AuthScreenState.Authorized
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome, ${state.firstName} ${state.lastName}!",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

