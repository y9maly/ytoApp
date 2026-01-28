package me.maly.y9to.screen.myProfile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import me.maly.y9to.compose.ContentPadding
import me.maly.y9to.compose.EmptyContentPadding
import me.maly.y9to.compose.components.accentEditable.AccentEditableOption
import me.maly.y9to.compose.components.accentEditable.AccentEditableTextField
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileScreen
import me.maly.y9to.compose.time.LocalClock
import me.maly.y9to.compose.time.LocalTimeZone
import me.maly.y9to.compose.time.rememberLocalDateTime
import me.maly.y9to.compose.time.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import pro.respawn.flowmvi.compose.dsl.subscribe
import y9to.common.types.Birthday
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.cat1
import y9to.composeapp.generated.resources.close
import y9to.composeapp.generated.resources.edit
import y9to.libs.stdlib.optional.present
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    vm: MyProfileViewModel,
    modifier: Modifier = Modifier,
    screenState: MyProfileState = rememberMyProfileScreenState(),
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val clock = LocalClock.current
    val currentYear = remember(clock) { clock.now() }.rememberLocalDateTime().year

    val store = vm.store
    val state by store.subscribe()
    val scope = rememberCoroutineScope()

    when (state) {
        is MyProfileScreenState.Edit -> screenState.editing = true
        else -> screenState.editing = false
    }

    var showFullNameBottomSheet by remember { mutableStateOf(false) }
    var showBirthdayDialog by remember { mutableStateOf(false) }
    val fullNameBottomSheetState = rememberModalBottomSheetState()
    val birthdayPickerState = rememberBirthdayPickerState(currentYear)

    when (val state = state) {
        is MyProfileScreenState.Content -> {
            LaunchedEffect(state.myProfile.birthday) {
                val birthday = state.myProfile.birthday
                if (birthday == null) {
                    birthdayPickerState.selectedDateMillis = clock.now().toEpochMilliseconds()
                } else {
                    val dateTime = birthday.toLocalDate(currentYear).atTime(0, 0)
                    val instant = dateTime.toInstant(TimeZone.UTC)
                    birthdayPickerState.selectedDateMillis = instant.toEpochMilliseconds()
                }
            }

            MyProfileScreen(
                modifier = modifier,
                contentPadding = contentPadding,
                state = state,
                enterEditMode = {
                    store.intent(MyProfileScreenIntent.EnterEditMode)
                },
                exitEditMode = { applyChanges ->
                    store.intent(MyProfileScreenIntent.ExitEditMode(applyChanges))
                },
                onChangeFullName = {
                    showFullNameBottomSheet = true
                },
                onChangeBio = { newBio ->
                    store.intent(MyProfileScreenIntent.Edit(bio = present(newBio)))
                },
                onChangeBirthday = {
                    showBirthdayDialog = true
                }
            )
        }

        is MyProfileScreenState.Error -> {
            Box(Modifier.fillMaxSize()) {
                Text(state.message, Modifier.align(Center))
            }
        }

        is MyProfileScreenState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                LoadingIndicator(Modifier.align(Center))
            }
        }
    }

    // birthdayDialog
    if (showBirthdayDialog) {
        DatePickerDialog(
            onDismissRequest = { showBirthdayDialog = false },
            confirmButton = {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    OutlinedButton({
                        store.intent(MyProfileScreenIntent.Edit(
                            birthday = present(null)
                        ))
                        showBirthdayDialog = false
                    }) {
                        Text("Delete")
                    }

                    Spacer(Modifier.weight(1f))

                    Button({
                        val selectedDateMillis = birthdayPickerState.selectedDateMillis
                            ?: return@Button
                        val selectedDate = Instant.fromEpochMilliseconds(selectedDateMillis)
                            .toLocalDateTime(TimeZone.UTC)
                        val year = selectedDate.year.takeIf { it < currentYear }
                        val month = selectedDate.month
                        val day = selectedDate.day
                        store.intent(
                            MyProfileScreenIntent.Edit(
                                birthday = present(Birthday(year, month, day))
                            )
                        )
                        showBirthdayDialog = false
                    }) {
                        Text("Save")
                    }
                }
            }
        ) {
            DatePicker(birthdayPickerState)
        }
    }

    // fullNameBottomSheet
    run {
        if (!showFullNameBottomSheet)
            return@run
        val myProfile = (state as? MyProfileScreenState.Content)?.myProfile
            ?: return@run

        ModalBottomSheet(
            sheetState = fullNameBottomSheetState,
            onDismissRequest = {
                scope.launch {
                    fullNameBottomSheetState.hide()
                    showFullNameBottomSheet = false
                }
            },
        ) {
            ChangeNameBottomSheet(
                initialFirstName = myProfile.firstName,
                initialLastName = myProfile.lastName ?: "",
                onSave = { firstName, lastName ->
                    store.intent(MyProfileScreenIntent.Edit(
                        firstName = present(firstName),
                        lastName = present(lastName)
                    ))
                    scope.launch {
                        fullNameBottomSheetState.hide()
                        showFullNameBottomSheet = false
                    }
                },
                onDismiss = {
                    scope.launch {
                        fullNameBottomSheetState.hide()
                        showFullNameBottomSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun rememberBirthdayPickerState(currentYear: Int): DatePickerState {
    return rememberDatePickerState(
        yearRange = 1900..currentYear,
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean {
                    return year in 1900..currentYear
                }

                override fun isSelectableDate(utcTimeMillis: Long) = true
//                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//                    val now = clock.now()
//                    return utcTimeMillis < now.toEpochMilliseconds()
//                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeNameBottomSheet(
    initialFirstName: String,
    initialLastName: String,
    onSave: (firstName: String, lastName: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf(initialFirstName) }
    var lastName by remember { mutableStateOf(initialLastName) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileScreen(
    state: MyProfileScreenState.Content,
    enterEditMode: () -> Unit,
    exitEditMode: (applyChanges: Boolean) -> Unit,
    onChangeFullName: () -> Unit,
    onChangeBio: (String?) -> Unit,
    onChangeBirthday: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val editMode by rememberUpdatedState(state is MyProfileScreenState.Edit)

    TemplateProfileScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        firstName = state.uiFirstDisplayName,
        lastName = null,
        cover = {
            Image(painterResource(Res.drawable.cat1), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        },
        avatar = {
            Image(painterResource(Res.drawable.cat1), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        },
        coverOverlay = CoverOverlay.Default(
            trailingIcon = {
                val buttonEnabled by rememberUpdatedState(scrollBehavior.state.collapsedFraction < 0.2f)
                val alpha = 1f - scrollBehavior.state.collapsedFraction

                IconButton(
                    modifier = Modifier.alpha(alpha),
                    enabled = buttonEnabled,
                    onClick = { if (editMode) exitEditMode(true) else enterEditMode() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        disabledContainerColor = Color.Black.copy(alpha = 0.5f),
                    ),
                ) {
                    AnimatedContent(editMode) { editMode ->
                        if (editMode)
                            Icon(painterResource(Res.drawable.close), null, tint = Color.White)
                        else
                            Icon(painterResource(Res.drawable.edit), null, tint = Color.White)
                    }
                }
            }
        ),
        avatarOverlay = AvatarOverlay.Default,
        scrollBehavior = scrollBehavior,
        canChangeFullName = editMode,
        onChangeFullName = onChangeFullName,
    ) {
        val lookaheadScope: LookaheadScope = this

        item("User id") {
            AccentEditableOption(
                modifier = Modifier.combinedClickable(
                    onLongClick = {
                        // todo copy popup
                    }
                ) {},
                name = "User id",
                value = state.myProfile.userId,
                lookaheadScope = lookaheadScope,
            )
        }

        item("Phone number") {
            AnimatedShrink(state.myProfile.phoneNumber != null) {
                val phoneNumber = remember { mutableStateOf(state.myProfile.phoneNumber) }
                    .apply { value = state.myProfile.phoneNumber ?: value }
                    .value ?: return@AnimatedShrink

                AccentEditableOption(
                    name = "Phone number",
                    value = phoneNumber,
                    lookaheadScope = lookaheadScope,
                    editMode = editMode
                )
            }
        }

        item("Email") {
            AnimatedShrink(state.myProfile.email != null) {
                val email = remember { mutableStateOf(state.myProfile.email) }
                    .apply { value = state.myProfile.email ?: value }
                    .value ?: return@AnimatedShrink

                AccentEditableOption(
                    name = "Email",
                    value = email,
                    lookaheadScope = lookaheadScope,
                    editMode = editMode
                )
            }
        }

        item("Bio") {
            AnimatedShrink(!state.uiFirstBio.isNullOrEmpty() || editMode) {
                val bio = state.uiFirstBio ?: ""

                var bioTextFieldValue by remember { mutableStateOf(TextFieldValue(bio)) }

                AccentEditableTextField(
                    name = "Bio",
                    value = bioTextFieldValue,
                    onChangeValue = { newBio ->
                        bioTextFieldValue = newBio
                        onChangeBio(newBio.text.takeIf { it.isNotBlank() })
                    },
                    hint = "Write a few words about yourself...",
                    editMode = editMode,
                )
            }
        }

        item("Birthday") {
            AnimatedShrink(state.uiFirstBirthday != null || editMode) {
                val birthday = state.uiFirstBirthday

                val value = when {
                    birthday == null -> "Not set"

                    birthday.year == null -> {
                        val dayOnMonth = birthday.dayOfMonth.toString()
                            .let { if (it.length == 1) "0$it" else it }
                        val month = birthday.month.name.lowercase()
                            .let { it[0].uppercase() + it.drop(1) }
                        "$dayOnMonth $month"
                    }

                    else -> {
                        val dayOnMonth = birthday.dayOfMonth.toString()
                            .let { if (it.length == 1) "0$it" else it }
                        val month = birthday.month.name.lowercase()
                            .let { it[0].uppercase() + it.drop(1) }
                        val year = birthday.year.toString()
                        "$dayOnMonth $month, $year"
                    }
                }

                AccentEditableOption(
                    name = "Birthday",
                    value = value,
                    editMode = if (birthday == null) true else editMode,
                    trailing = {
                        AnimatedAlpha(editMode) {
                            TextButton(onChangeBirthday) {
                                Text(
                                    if (birthday == null) "Set"
                                    else "Change"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AnimatedShrink(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter =
            fadeIn() +
            expandIn(expandFrom = Alignment.TopStart) { IntSize(it.width, 0) },
        exit =
            fadeOut() +
            shrinkOut(shrinkTowards = Alignment.TopStart) { IntSize(it.width, 0) },
    ) {
        content()
    }
}

@Composable
private fun AnimatedAlpha(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        content()
    }
}
