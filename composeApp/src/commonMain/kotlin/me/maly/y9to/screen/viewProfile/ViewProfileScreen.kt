package me.maly.y9to.screen.viewProfile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import me.maly.y9to.compose.components.accentEditable.AccentEditableOption
import me.maly.y9to.compose.components.templates.profileScreen.AvatarOverlay
import me.maly.y9to.compose.components.templates.profileScreen.CoverOverlay
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileScreen
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileItemScope
import me.maly.y9to.compose.utils.ContentPadding
import me.maly.y9to.compose.utils.EmptyContentPadding
import me.maly.y9to.types.UiProfile
import me.maly.y9to.viewModel.ViewProfileUiState
import me.maly.y9to.viewModel.ViewProfileViewModel
import org.jetbrains.compose.resources.painterResource
import y9to.common.types.Birthday
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.arrow_back


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
    state: ViewProfileUiState.Content,
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null,
    contentPadding: ContentPadding = EmptyContentPadding,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    TemplateProfileScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        firstName = state.profile.firstName,
        lastName = state.profile.lastName,
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
        coverOverlay = coverOverlayOf(
            scrollBehavior = scrollBehavior,
            navigateBack = navigateBack,
        ),
        avatarOverlay = AvatarOverlay.Default,
        scrollBehavior = scrollBehavior,
        canChangeFullName = false,
    ) {
        viewProfileItems(
            lookaheadScope = this,
            profile = state.profile,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun coverOverlayOf(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateBack: (() -> Unit)?,
): CoverOverlay {
    val navigateBackAction by rememberUpdatedState(navigateBack ?: return CoverOverlay.Default())
    val scrollBehavior by rememberUpdatedState(scrollBehavior)

    val leadingIcon = remember {
        @Composable {
            val buttonEnabled by rememberUpdatedState(scrollBehavior.state.collapsedFraction < 0.2f)
            val alpha = 1f - scrollBehavior.state.collapsedFraction

            IconButton(
                modifier = Modifier.alpha(alpha),
                enabled = buttonEnabled,
                onClick = navigateBackAction,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    disabledContainerColor = Color.Black.copy(alpha = 0.5f),
                ),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
        }
    }

    return CoverOverlay.Default(leadingIcon = leadingIcon)
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
