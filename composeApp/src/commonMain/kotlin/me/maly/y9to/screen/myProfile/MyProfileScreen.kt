package me.maly.y9to.screen.myProfile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import me.maly.y9to.compose.ContentPadding
import me.maly.y9to.compose.EmptyContentPadding
import me.maly.y9to.compose.components.accentEditable.AccentEditableOption
import me.maly.y9to.compose.components.accentEditable.AccentEditableTextField
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileScreen
import me.maly.y9to.compose.time.LocalClock
import me.maly.y9to.compose.time.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import pro.respawn.flowmvi.compose.dsl.subscribe
import pro.respawn.flowmvi.util.typed
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.cat1
import y9to.composeapp.generated.resources.close
import y9to.composeapp.generated.resources.edit
import y9to.libs.stdlib.optional.present


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    vm: MyProfileViewModel,
    modifier: Modifier = Modifier,
    screenState: MyProfileState = rememberMyProfileScreenState(),
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val store = vm.store
    val state by store.subscribe()

    when (state) {
        is MyProfileUiState.Edit -> screenState.editing = true
        else -> screenState.editing = false
    }

    var showFullNameBottomSheet by remember { mutableStateOf(false) }
    var showBirthdayDialog by remember { mutableStateOf(false) }
    var showConfirmLogOutDialog by remember { mutableStateOf(false) }
    var showLoggingOutDialog by remember { mutableStateOf(false) }


    when (val state = state) {
        is MyProfileUiState.Content -> {
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
                },
                onLogOut = {
                    showConfirmLogOutDialog = true
                },
            )
        }

        is MyProfileUiState.Error -> {
            Box(Modifier.fillMaxSize()) {
                Text(state.message, Modifier.align(Center))
            }
        }

        is MyProfileUiState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                LoadingIndicator(Modifier.align(Center))
            }
        }
    }

    ChangeBirthdayDialog(
        visible = showBirthdayDialog,
        initial = state.typed<MyProfileUiState.Content>()?.uiFirstBirthday,
        onSave = {
            showBirthdayDialog = false
            store.intent(MyProfileScreenIntent.Edit(
                birthday = present(it)
            ))
        },
        onDismiss = {
            showBirthdayDialog = false
        }
    )

    when (val state = state) {
        is MyProfileUiState.Content -> {
            ChangeFullNameBottomSheet(
                visible = showFullNameBottomSheet,
                initialFirstName = state.uiFirstFirstName,
                initialLastName = state.uiFirstLastName,
                onSave = { firstName, lastName ->
                    showFullNameBottomSheet = false
                    store.intent(MyProfileScreenIntent.Edit(
                        firstName = present(firstName),
                        lastName = present(lastName)
                    ))
                },
                onDismiss = {
                    showFullNameBottomSheet = false
                }
            )
        }

        else -> {}
    }

    ConfirmLogOutDialog(
        visible = showConfirmLogOutDialog,
        onConfirm = {
            showConfirmLogOutDialog = false
            showLoggingOutDialog = true
            store.intent(MyProfileScreenIntent.LogOut)
        },
        onDismiss = {
            showConfirmLogOutDialog = false
        },
    )

    LoggingOutDialog(
        visible = showLoggingOutDialog,
    )
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileScreen(
    state: MyProfileUiState.Content,
    enterEditMode: () -> Unit,
    exitEditMode: (applyChanges: Boolean) -> Unit,
    onChangeFullName: () -> Unit,
    onChangeBio: (String?) -> Unit,
    onChangeBirthday: () -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val editMode by rememberUpdatedState(state is MyProfileUiState.Edit)

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

        item("Log out") {
            AnimatedShrink(editMode) {
                OutlinedButton(
                    onClick = onLogOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.error,
                    )
                ) {
                    Text("Log out")
                }
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
