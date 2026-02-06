package me.maly.y9to.screen.myProfile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import me.maly.y9to.compose.ContentPadding
import me.maly.y9to.compose.EmptyContentPadding
import me.maly.y9to.compose.components.accentEditable.AccentEditableOption
import me.maly.y9to.compose.components.accentEditable.AccentEditableTextField
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileItemScope
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileScreen
import me.maly.y9to.compose.dropTop
import me.maly.y9to.compose.plusAll
import me.maly.y9to.compose.rememberFilePickerLauncher
import me.maly.y9to.types.UiUploadAvatarError
import me.maly.y9to.types.UiUploadAvatarState
import me.maly.y9to.types.UiUploadCoverError
import me.maly.y9to.types.UiUploadCoverState
import org.jetbrains.compose.resources.painterResource
import pro.respawn.flowmvi.util.typed
import y9to.common.types.Birthday
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_circle_up
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
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val coverFilePickerLauncher = rememberFilePickerLauncher(FileKitType.Image, FileKitMode.Single) { file ->
        file ?: return@rememberFilePickerLauncher
        vm.uploadCover(file)
    }

    val avatarFilePickerLauncher = rememberFilePickerLauncher(FileKitType.Image, FileKitMode.Single) { file ->
        file ?: return@rememberFilePickerLauncher
        vm.uploadAvatar(file)
    }

    LaunchedEffect(vm) {
        vm.actions.collect { action ->
            when (action) {
                is MyProfileScreenAction.ShowMessage -> {
                    snackbarHostState.showSnackbar(action.text)
                }
            }
        }
    }

    var showFullNameBottomSheet by remember { mutableStateOf(false) }
    var showBirthdayDialog by remember { mutableStateOf(false) }
    var showConfirmLogOutDialog by remember { mutableStateOf(false) }
    var showCantLogOutDialog by remember { mutableStateOf(false) }
    var showLoggingOutDialog by remember { mutableStateOf(false) }

    when (val state = state) {
        is MyProfileUiState.Content -> {
            MyProfileScreen(
                modifier = modifier,
                contentPadding = contentPadding,
                state = state,
                editMode = screenState.editMode,
                snackbarHostState = snackbarHostState,
                enterEditMode = {
                    screenState.editMode = true
                },
                exitEditMode = { applyChanges ->
                    screenState.editMode = false

                    if (applyChanges)
                        vm.applyChanges()
                    else
                        vm.discardChanges()
                },
                uploadCover = {
                    coverFilePickerLauncher.launch()
                },
                uploadAvatar = {
                    avatarFilePickerLauncher.launch()
                },
                cancelUploadCover = {
                    vm.cancelUploadCover()
                },
                cancelUploadAvatar = {
                    vm.cancelUploadAvatar()
                },
                retryUploadCover = null,
                retryUploadAvatar = null,
                onChangeFullName = {
                    showFullNameBottomSheet = true
                },
                onChangeBio = { newBio ->
                    vm.edit(bio = present(newBio))
                },
                onChangeBirthday = {
                    showBirthdayDialog = true
                },
                onLogOut = {
                    if (state.canLogOut) {
                        showConfirmLogOutDialog = true
                    } else {
                        showCantLogOutDialog = true
                    }
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
            vm.edit(birthday = present(it))
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
                    vm.edit(firstName = present(firstName), lastName = present(lastName))
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
            vm.logOut()
        },
        onDismiss = {
            showConfirmLogOutDialog = false
        },
    )

    CantLogOutDialog(
        visible = showCantLogOutDialog,
        onDismiss = { showCantLogOutDialog = false },
    )

    LoggingOutDialog(
        visible = showLoggingOutDialog,
    )
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileScreen(
    editMode: Boolean,
    state: MyProfileUiState.Content,
    snackbarHostState: SnackbarHostState,
    enterEditMode: () -> Unit,
    exitEditMode: (applyChanges: Boolean) -> Unit,
    uploadCover: () -> Unit,
    uploadAvatar: () -> Unit,
    cancelUploadCover: () -> Unit,
    cancelUploadAvatar: () -> Unit,
    retryUploadCover: (() -> Unit)?,
    retryUploadAvatar: (() -> Unit)?,
    onChangeFullName: () -> Unit,
    onChangeBio: (String?) -> Unit,
    onChangeBirthday: () -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val coverOverlay = coverOverlayOf(
        state = state,
        editMode = editMode,
        scrollBehavior = scrollBehavior,
        upload = uploadCover,
        cancelUpload = cancelUploadCover,
        retryUpload = retryUploadCover,
        enterEditMode = enterEditMode,
        exitEditMode = exitEditMode,
    )

    val avatarOverlay = avatarOverlayOf(
        state = state,
        editMode = editMode,
        upload = uploadAvatar,
        cancelUpload = cancelUploadAvatar,
        retryUpload = retryUploadAvatar,
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState, Modifier.padding(contentPadding.dropTop()))
        },
    ) { scaffoldContentPadding ->
        TemplateProfileScreen(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding plusAll scaffoldContentPadding,
            firstName = state.uiFirstDisplayName,
            lastName = null,
            cover = {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = state.uiFirstMyProfile.cover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            },
            avatar = {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = state.uiFirstMyProfile.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            },
            coverOverlay = coverOverlay,
            avatarOverlay = avatarOverlay,
            scrollBehavior = scrollBehavior,
            canChangeFullName = editMode,
            onChangeFullName = onChangeFullName,
        ) {
            myProfileItems(
                editMode = editMode,
                lookaheadScope = this,
                userId = state.uiFirstMyProfile.userId,
                phoneNumber = state.uiFirstMyProfile.phoneNumber,
                email = state.uiFirstMyProfile.email,
                bio = state.uiFirstMyProfile.bio,
                birthday = state.uiFirstMyProfile.birthday,
                onChangeBio = onChangeBio,
                onChangeBirthday = onChangeBirthday,
                onLogOut = onLogOut,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun coverOverlayOf(
    state: MyProfileUiState.Content,
    editMode: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    upload: () -> Unit,
    cancelUpload: () -> Unit,
    retryUpload: (() -> Unit)?,
    enterEditMode: () -> Unit,
    exitEditMode: (applyChanges: Boolean) -> Unit,
): CoverOverlay {
    val editMode by rememberUpdatedState(editMode)
    val scrollBehavior by rememberUpdatedState(scrollBehavior)
    val enterEditMode by rememberUpdatedState(enterEditMode)
    val exitEditMode by rememberUpdatedState(exitEditMode)

    val leadingIcon = remember {
        @Composable {
            val buttonEnabled by rememberUpdatedState(scrollBehavior.state.collapsedFraction < 0.2f)
            val alpha = 1f - scrollBehavior.state.collapsedFraction

            AnimatedVisibility(
                editMode,
                enter = fadeIn() + scaleIn(initialScale = .8f),
                exit = fadeOut() + scaleOut(targetScale = .8f),
            ) {
                IconButton(
                    modifier = Modifier.alpha(alpha),
                    enabled = buttonEnabled,
                    onClick = { exitEditMode(false) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f),
                        disabledContainerColor = Color.Black.copy(alpha = 0.5f),
                    ),
                ) {
                    Icon(painterResource(Res.drawable.close), null, tint = Color.White)
                }
            }
        }
    }

    val trailingIcon = remember {
        @Composable {
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
                        Icon(painterResource(Res.drawable.arrow_circle_up), null, tint = Color.White)
                    else
                        Icon(painterResource(Res.drawable.edit), null, tint = Color.White)
                }
            }
        }
    }

    when (val uploadCoverState = state.uploadCoverState) {
        UiUploadCoverState.None -> {}

        is UiUploadCoverState.Error -> return CoverOverlay.UploadError(
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            explanation = when (val error = uploadCoverState.error) {
                is UiUploadCoverError.TooBigFile -> "Too big file"
                is UiUploadCoverError.StorageQuotaExceeded -> "Storage quota exceeded"
                is UiUploadCoverError.ConnectionError -> "Connection error"
                is UiUploadCoverError.UnknownError -> error.message
            },
            cancelUpload = cancelUpload,
            tryAnotherOne = upload,
            retry = when (uploadCoverState.error) {
                is UiUploadCoverError.TooBigFile -> null

                is UiUploadCoverError.StorageQuotaExceeded,
                is UiUploadCoverError.ConnectionError,
                is UiUploadCoverError.UnknownError -> retryUpload
            }
        )

        is UiUploadCoverState.Uploading -> return CoverOverlay.Uploading(
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            cancelUpload = cancelUpload,
        )
    }

    if (editMode)
        return CoverOverlay.CanUpload(
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            upload = upload,
        )

    return CoverOverlay.Default(
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun avatarOverlayOf(
    state: MyProfileUiState.Content,
    editMode: Boolean,
    upload: () -> Unit,
    cancelUpload: () -> Unit,
    retryUpload: (() -> Unit)?,
): AvatarOverlay {
    val editMode by rememberUpdatedState(editMode)

    when (val uploadAvatarState = state.uploadAvatarState) {
        is UiUploadAvatarState.None -> {}

        is UiUploadAvatarState.Uploading -> return AvatarOverlay.Uploading(
            cancelUpload = cancelUpload,
        )

        is UiUploadAvatarState.Error -> return AvatarOverlay.UploadError(
            cancelUpload = cancelUpload,
            tryAnotherOne = upload,
            retry = retryUpload,
            explanation = when (val error = uploadAvatarState.error) {
                is UiUploadAvatarError.TooBigFile -> "Too big file"
                is UiUploadAvatarError.StorageQuotaExceeded -> "Storage quota exceeded"
                is UiUploadAvatarError.ConnectionError -> "Connection error"
                is UiUploadAvatarError.UnknownError -> error.message
            },
        )
    }

    if (editMode)
        return AvatarOverlay.CanUpload(
            upload = upload,
        )

    return AvatarOverlay.Default
}

private fun TemplateProfileItemScope.myProfileItems(
    editMode: Boolean,
    lookaheadScope: LookaheadScope,
    userId: String,
    phoneNumber: String?,
    email: String?,
    bio: String?,
    birthday: Birthday?,
    onChangeBio: (String?) -> Unit,
    onChangeBirthday: () -> Unit,
    onLogOut: () -> Unit,
) {
    item("User id") {
        AccentEditableOption(
            modifier = Modifier.combinedClickable(
                onLongClick = {
                    // todo copy popup
                }
            ) {},
            name = "User id",
            value = userId,
            lookaheadScope = lookaheadScope,
        )
    }

    item("Phone number") {
        AnimatedShrink(phoneNumber != null) {
            val phoneNumber = remember { mutableStateOf(phoneNumber) }
                .apply { value = phoneNumber ?: value }
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
        AnimatedShrink(email != null) {
            val email = remember { mutableStateOf(email) }
                .apply { value = email ?: value }
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
        AnimatedShrink(!bio.isNullOrEmpty() || editMode) {
            val bio = bio ?: ""

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
        AnimatedShrink(birthday != null || editMode) {
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
                        TextButton(onClick = onChangeBirthday) {
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
