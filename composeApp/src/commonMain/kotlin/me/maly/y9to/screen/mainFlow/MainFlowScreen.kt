package me.maly.y9to.screen.mainFlow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.serialization.Serializable
import me.maly.y9to.compose.viewModel.injectViewModel
import me.maly.y9to.screen.feed.FeedScreen
import me.maly.y9to.screen.feed.FeedViewModel
import me.maly.y9to.screen.myProfile.MyProfileScreen
import me.maly.y9to.screen.myProfile.MyProfileViewModel
import me.maly.y9to.screen.myProfile.rememberMyProfileScreenState
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.filter_list
import y9to.composeapp.generated.resources.settings

@Serializable
enum class Tab {
    Feed, MyProfile;

    companion object {
        val Saver = Saver<Tab, Int>(
            save = { it.ordinal },
            restore = { ordinal ->
                entries.first { it.ordinal == ordinal }
            }
        )
    }
}

private val tabs = listOf(
    Tab.Feed,
    Tab.MyProfile,
)

@Composable
fun MainFlowScreen(
    vm: MainFlowViewModel,
    authenticate: () -> Unit,
    modifier: Modifier = Modifier,
    navigatePostDetails: (postId: String) -> Unit = {},
) {
    var profileEditing by remember { mutableStateOf(false) }
    val navbarVisible by remember { derivedStateOf { !profileEditing } }
    val isAuthenticated by vm.isAuthenticated.collectAsState(null)
    val stateHolder = rememberSaveableStateHolder()
    val hazeState = rememberHazeState()
    var tab by remember { mutableStateOf(Tab.Feed) }

    LaunchedEffect(tab, isAuthenticated) {
        if (isAuthenticated == false && tab != Tab.Feed) {
            tab = Tab.Feed
        }
    }

    Scaffold(
        modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = navbarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                Box {
                    Box(Modifier
                        .matchParentSize()
                        .hazeEffect(hazeState) {
                            progressive = HazeProgressive.verticalGradient(startIntensity = 0f, endIntensity = 1f)
                        }
                    )

                    NavigationBar(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(100))
                    ) {
                        NavigationBarItem(
                            selected = tab == Tab.Feed,
                            onClick = { tab = Tab.Feed },
                            icon = { Icon(painterResource(Res.drawable.filter_list), null) },
                            label = { Text("Feed") }
                        )

                        NavigationBarItem(
                            selected = tab == Tab.MyProfile,
                            onClick = {
                                when (isAuthenticated) {
                                    null -> {}
                                    true -> tab = Tab.MyProfile
                                    false -> authenticate()
                                }
                            },
                            icon = { Icon(painterResource(Res.drawable.settings), null) },
                            label = { Text("Settings") }
                        )
                    }
                }
            }
        }
    ) { scaffoldPadding ->
        AnimatedContent(
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
            targetState = tab,
            transitionSpec = {
                if (tabs.indexOf(initialState) < tabs.indexOf(targetState)) {
                    val initial = slideOutHorizontally { -it }
                    val target = slideInHorizontally { it }
                    target togetherWith initial
                } else {
                    val initial = slideOutHorizontally { it }
                    val target = slideInHorizontally { -it }
                    target togetherWith initial
                }
            }
        ) { tab ->
            Box(Modifier.fillMaxSize()) {
                stateHolder.SaveableStateProvider(tab) {
                    val feedVM = injectViewModel<FeedViewModel>()
                    val myProfileVM = injectViewModel<MyProfileViewModel>()

                    when (tab) {
                        Tab.Feed -> {
                            FeedScreen(
                                vm = feedVM,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = scaffoldPadding,
                                navigatePostDetails = navigatePostDetails,
                            )
                        }

                        Tab.MyProfile -> {
                            val state = rememberMyProfileScreenState()

                            DisposableEffect(state.editing) {
                                profileEditing = state.editing
                                onDispose { profileEditing = false }
                            }

                            MyProfileScreen(
                                vm = myProfileVM,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = scaffoldPadding,
                                screenState = state,
                            )
                        }
                    }
                }
            }
        }
    }
}
