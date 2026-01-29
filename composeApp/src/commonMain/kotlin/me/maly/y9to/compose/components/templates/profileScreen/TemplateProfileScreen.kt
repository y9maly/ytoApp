package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.dp
import me.maly.y9to.compose.ContentPadding
import me.maly.y9to.compose.EmptyContentPadding
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.compose.takeBottom


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateProfileScreen(
    firstName: String,
    lastName: String?,
    cover: @Composable () -> Unit,
    avatar: @Composable () -> Unit,
    coverOverlay: CoverOverlay,
    avatarOverlay: AvatarOverlay,
    modifier: Modifier = Modifier,
    canChangeFullName: Boolean = false,
    onChangeFullName: () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    contentPadding: ContentPadding = EmptyContentPadding,
    items: TemplateProfileItemScope.() -> Unit = {}
) {
    val displayName = lastName?.let { "$firstName $lastName" } ?: firstName
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            // на маке на трекпаде сломан nested scroll, мб ещё на мышке я хз.
            // CMP-4975. Не приходит onPreScroll.
            // Это фикс, я не знаю как он работает, просто перебирал варианты и так заработало. its strange
            .scrollable(ScrollableState {
                -scrollState.dispatchRawDelta(-it)
            }, Orientation.Vertical),
        containerColor = Color.Transparent,
        topBar = {
            Header(
                scrollBehavior = scrollBehavior,
                cover = cover,
                avatar = avatar,
                coverOverlay = coverOverlay,
                avatarOverlay = avatarOverlay,
                displayName = HeaderDefaults.displayNameWithEdit(
                    string = displayName,
                    editable = canChangeFullName,
                    onEdit = onChangeFullName
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    ) { scaffoldPadding ->
        Box(Modifier
            .padding(contentPadding.dropBottom())
            .padding(scaffoldPadding.dropBottom())
        ) {
            LookaheadScope {
                val items by remember(items) {
                    derivedStateOf {
                        TemplateProfileItemScopeImpl(this).apply(items).items
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState, enabled = false),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment
                ) {
                    Spacer(Modifier.height(8.dp))

                    items.forEach { (key, content) ->
                        key(key) { content() }
                    }

                    Spacer(Modifier
                        .padding(contentPadding.takeBottom())
                        .padding(scaffoldPadding.takeBottom())
                    )
                }
            }
        }
    }
}
