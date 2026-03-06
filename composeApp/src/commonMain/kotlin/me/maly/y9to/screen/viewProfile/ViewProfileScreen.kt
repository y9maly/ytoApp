package me.maly.y9to.screen.viewProfile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import me.maly.y9to.compose.asyncImage.AsyncImage
import me.maly.y9to.compose.components.accentEditable.AccentEditableOption
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileScreen
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileItemScope
import me.maly.y9to.compose.components.writePost.WriteAccount
import me.maly.y9to.compose.components.writePost.WritePostContainer
import me.maly.y9to.compose.components.writePost.rememberWritePostState
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.EmptyContentPadding
import me.maly.y9to.compose.utils.collectAsStateUndispatched
import me.maly.y9to.compose.utils.dropTop
import me.maly.y9to.screen.feed.FeedColumn
import me.maly.y9to.screen.feed.UiInputPostContent
import me.maly.y9to.types.UiProfile
import me.maly.y9to.viewModel.ViewProfileUiState
import me.maly.y9to.viewModel.ViewProfileViewModel
import org.jetbrains.compose.resources.painterResource
import y9to.common.types.Birthday
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_back
import y9to.composeapp.generated.resources.cat1


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ViewProfileScreen(
    vm: ViewProfileViewModel,
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val state by vm.state.collectAsState()

    when (val state = state) {
        is ViewProfileUiState.Content -> ViewProfileScreen(
            vm = vm,
            state = state,
            modifier = modifier,
            navigateBack = navigateBack,
            contentPadding = contentPadding,
        )

        is ViewProfileUiState.Loading -> {
            Box(modifier.fillMaxSize()) {
                LoadingIndicator(Modifier.align(Center))
            }
        }

        is ViewProfileUiState.Error -> {
            Box(modifier.fillMaxSize()) {
                Text(state.message, Modifier.align(Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewProfileScreen(
    vm: ViewProfileViewModel,
    state: ViewProfileUiState.Content,
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val navigateBackLatest by rememberUpdatedState(navigateBack)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lazyPagingItems = vm.pagerFlow.collectAsLazyPagingItems()
    val myProfile by vm.myProfile.collectAsStateUndispatched(null)
    val writePostState = rememberWritePostState()
    val writeAccount = remember(myProfile) {
        val myProfile = myProfile ?: return@remember null
        WriteAccount.Personal(
            avatar = {
                Image(painterResource(Res.drawable.cat1), null, Modifier.fillMaxSize())
            },
            firstName = myProfile.firstName,
            lastName = myProfile.lastName,
        )
    }

    Column(modifier) {
        TemplateProfileScreen(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = contentPadding,
            firstName = state.profile.firstName,
            lastName = state.profile.lastName,
            navigateIcon = {
                IconButton(
                    modifier = Modifier.fillMaxHeight(),
                    onClick = { navigateBackLatest?.invoke() }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            },
            cover = {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = state.profile.cover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            },
            avatar = {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = state.profile.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            },
            coverOverlay = CoverOverlay.Default(),
            avatarOverlay = AvatarOverlay.Default,
            scrollBehavior = scrollBehavior,
            canChangeFullName = false,
        ) {
            viewProfileItems(
                lookaheadScope = this,
                profile = state.profile,
            )

            item("feed") {
                FeedColumn(
                    vm.publishPreviewItems,
                    vm.prependItems,
                    lazyPagingItems,
                    // todo todo todo todo todo todo todo todo todo todo
                    Modifier.heightIn(max = 1000.dp).onSizeChanged {
                        println(it)
                    },
//            gotoPostDetails = navigatePostDetails,
//            gotoAuthorProfile = navigateProfile,
//            openDropdownMenu = { post, position ->
//                postDropdownMenuState.show(post, feedColumnOffset + position)
//            },
                    contentPadding = contentPadding.dropTop()
                ) { postItems ->
                    item {
                        AnimatedVisibility(
                            visible = writeAccount != null,
                            enter =
                                fadeIn() + expandIn(expandFrom = Alignment.TopStart) { IntSize(it.width, 0) },
                            exit =
                                fadeOut() + shrinkOut(shrinkTowards = Alignment.TopStart) { IntSize(it.width, 0) },
                        ) {
                            var savedWriteAccount by remember { mutableStateOf(writeAccount) }
                            savedWriteAccount = writeAccount ?: savedWriteAccount
                            val currentWriteAccount = savedWriteAccount ?: return@AnimatedVisibility

                            Card {
                                WritePostContainer(
                                    state = writePostState,
                                    writeAccount = currentWriteAccount,
                                    onPublish = {
                                        vm.publish(UiInputPostContent.Standalone(writePostState.text.text))
                                        writePostState.text = TextFieldValue()
                                        writePostState.expanded = false
                                    }
                                )
                            }
                        }
                    }

                    postItems()
                }
            }
        }
    }
}

private fun TemplateProfileItemScope.viewProfileItems(
    lookaheadScope: LookaheadScope,
    profile: UiProfile,
) {
    item("User id") {
        AccentEditableOption(
            name = "User id",
            value = profile.userId,
            lookaheadScope = lookaheadScope,
        )
    }

    item("Phone number") {
        AnimatedShrink(profile.phoneNumber != null) {
            AccentEditableOption(
                name = "Phone number",
                value = profile.phoneNumber ?: return@AnimatedShrink,
                lookaheadScope = lookaheadScope,
            )
        }
    }

    item("Email") {
        AnimatedShrink(profile.email != null) {
            AccentEditableOption(
                name = "Email",
                value = profile.email ?: return@AnimatedShrink,
                lookaheadScope = lookaheadScope,
            )
        }
    }

    item("Bio") {
        AnimatedShrink(!profile.bio.isNullOrBlank()) {
            AccentEditableOption(
                name = "Bio",
                value = profile.bio ?: return@AnimatedShrink,
                lookaheadScope = lookaheadScope,
            )
        }
    }

    item("Birthday") {
        AnimatedShrink(profile.birthday != null) {
            val birthday = profile.birthday ?: return@AnimatedShrink

            AccentEditableOption(
                name = "Birthday",
                value = birthday.toDisplayString(),
                lookaheadScope = lookaheadScope,
            )
        }
    }
}

private fun Birthday.toDisplayString(): String {
    val dayOnMonth = dayOfMonth.toString().padStart(2, '0')
    val month = month.name.lowercase().replaceFirstChar(Char::uppercase)

    return when (val year = year) {
        null -> "$dayOnMonth $month"
        else -> "$dayOnMonth $month, $year"
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
