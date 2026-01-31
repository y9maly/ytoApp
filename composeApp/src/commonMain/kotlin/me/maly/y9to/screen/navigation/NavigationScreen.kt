@file:Suppress("SimplifyBooleanWithConstants")

package me.maly.y9to.screen.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.filter
import me.maly.y9to.compose.viewModel.injectViewModel
import me.maly.y9to.navigation.destination.AuthDestination
import me.maly.y9to.navigation.destination.Destination
import me.maly.y9to.navigation.destination.FeedDestination
import me.maly.y9to.navigation.destination.MainFlowDestination
import me.maly.y9to.navigation.destination.PostDetailsDestination
import me.maly.y9to.screen.auth.AuthScreen
import me.maly.y9to.screen.feed.FeedScreen
import me.maly.y9to.screen.mainFlow.MainFlowScreen
import me.maly.y9to.screen.postDetails.PostDetailsScreen
import org.koin.core.parameter.parametersOf


@Composable
fun NavigationScreen(
    vm: NavigationViewModel,
    backStack: MutableList<Destination>,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        vm.isAuthenticated.filter { it == true }.collect {
            backStack.clear()
            backStack.add(MainFlowDestination)
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        },
        popTransitionSpec = {
            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
        },
        entryProvider = entryProvider {
            navigationEntries(backStack)
        }
    )
}

private fun EntryProviderScope<Destination>.navigationEntries(
    backStack: MutableList<Destination>,
) {
    entry<AuthDestination> {
        AuthScreen(injectViewModel(), Modifier.fillMaxSize())
    }

    entry<MainFlowDestination> {
        MainFlowScreen(injectViewModel(), authenticate = {
            backStack.clear()
            backStack.add(AuthDestination)
        }, navigatePostDetails = {
            backStack.add(PostDetailsDestination(it))
        }, modifier = Modifier.fillMaxSize())
    }

    entry<PostDetailsDestination> { destination ->
        PostDetailsScreen(
            injectViewModel { parametersOf(destination.postId) },
            Modifier.fillMaxSize(),
            navigatePostDetails = {
                backStack.add(PostDetailsDestination(it))
            },
            navigateBack = {
                backStack.removeLastOrNull()
            }
        )
    }

    entry<FeedDestination> {
        FeedScreen(
            injectViewModel(),
            Modifier.fillMaxSize(),
            navigatePostDetails = {
                backStack.add(PostDetailsDestination(it))
            },
        )
    }
}
