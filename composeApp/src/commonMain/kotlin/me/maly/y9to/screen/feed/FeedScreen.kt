package me.maly.y9to.screen.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import me.maly.y9to.compose.collectAsStateUndispatched
import me.maly.y9to.compose.components.writePost.WriteAccount
import me.maly.y9to.compose.components.writePost.WritePostContainer
import me.maly.y9to.compose.components.writePost.rememberWritePostState
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.compose.dropTop
import me.maly.y9to.compose.plus
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.cat1


@Composable
fun FeedScreen(
    vm: FeedViewModel,
    modifier: Modifier = Modifier,
    navigatePostDetails: (postId: String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val headerState by vm.header.state.collectAsState()
    val lazyPagingItems = vm.pagerFlow.collectAsLazyPagingItems()
    val myProfile by vm.profile.collectAsStateUndispatched(null)
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

    var feedColumnPosition by remember { mutableStateOf(Offset.Zero) }

    LookaheadScope {
        Box {
            Column(modifier.pullToRefresh(
                isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading,
                state = rememberPullToRefreshState(),
                onRefresh = { lazyPagingItems.refresh() }
            )) {
                FeedHeader(
                    modifier = Modifier.fillMaxWidth(),
                    state = headerState,
                    contentPadding = contentPadding.dropBottom().plus(vertical = 8.dp)
                )

                FeedColumn(
                    vm.prependPosts,
                    vm.prePublishPreviews,
                    lazyPagingItems,
                    Modifier.fillMaxSize()
                        .onGloballyPositioned {
                            feedColumnPosition = it.toLookaheadCoordinates().positionInParent()
                        },
                    gotoPostDetails = navigatePostDetails,
                    openDropdownMenu = { post, position ->

                    },
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