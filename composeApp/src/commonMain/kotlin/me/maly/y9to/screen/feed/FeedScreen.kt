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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import me.maly.y9to.compose.collectAsStateUndispatched
import me.maly.y9to.compose.components.dropdownMenu.rememberDropdownMenuState
import me.maly.y9to.compose.components.writePost.WriteAccount
import me.maly.y9to.compose.components.writePost.WritePostContainer
import me.maly.y9to.compose.components.writePost.rememberWritePostState
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.compose.dropTop
import me.maly.y9to.compose.plus
import me.maly.y9to.types.UiPost
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.cat1
import y9to.composeapp.generated.resources.delete_forever
import y9to.composeapp.generated.resources.edit
import y9to.composeapp.generated.resources.repeat
import y9to.composeapp.generated.resources.reply


@Composable
fun FeedScreen(
    vm: FeedViewModel,
    modifier: Modifier = Modifier,
    navigatePostDetails: (postId: String) -> Unit = {},
    onEdit: (postId: String) -> Unit = {},
    onDelete: (postId: String) -> Unit = {},
    onReply: (postId: String) -> Unit = {},
    onRepost: (postId: String) -> Unit = {},
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

    val postDropdownMenuState = rememberDropdownMenuState<UiPost>()

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
                        postDropdownMenuState.show(post, feedColumnPosition + position)
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

            DropdownMenu(
                expanded = postDropdownMenuState.value != null,
                offset = with(LocalDensity.current) {
                    DpOffset(
                        x = postDropdownMenuState.position.x.toDp(),
                        y = postDropdownMenuState.position.y.toDp()
                    )
                },
                onDismissRequest = { postDropdownMenuState.hide() }
            ) {
                val item = remember { mutableStateOf(postDropdownMenuState.value) }
                    .apply { value = postDropdownMenuState.value ?: value }
                    .value ?: return@DropdownMenu

                val canEdit by vm.canEdit(item).collectAsStateUndispatched(false)
                val canDelete by vm.canDelete(item).collectAsStateUndispatched(false)
                val canReply by vm.canReply(item).collectAsStateUndispatched(false)
                val canRepost by vm.canRepost(item).collectAsStateUndispatched(false)

                if (canReply) {
                    DropdownMenuItem(
                        onClick = { onReply(item.id) },
                        text = {
                            Row(verticalAlignment = CenterVertically) {
                                Icon(painterResource(Res.drawable.reply), null)
                                Spacer(Modifier.width(6.dp))
                                Text("Reply")
                            }
                        }
                    )
                }

                if (canEdit) {
                    DropdownMenuItem(
                        onClick = { onEdit(item.id) },
                        text = {
                            Row(verticalAlignment = CenterVertically) {
                                Icon(painterResource(Res.drawable.edit), null)
                                Spacer(Modifier.width(6.dp))
                                Text("Edit")
                            }
                        }
                    )
                }

                if (canRepost) {
                    DropdownMenuItem(
                        onClick = { onRepost(item.id) },
                        text = {
                            Row(verticalAlignment = CenterVertically) {
                                Icon(painterResource(Res.drawable.repeat), null)
                                Spacer(Modifier.width(6.dp))
                                Text("Repost")
                            }
                        }
                    )
                }

                if (canDelete) {
                    DropdownMenuItem(
                        onClick = { onDelete(item.id) },
                        text = {
                            Row(verticalAlignment = CenterVertically) {
                                Icon(painterResource(Res.drawable.delete_forever), null)
                                Spacer(Modifier.width(6.dp))
                                Text("Delete")
                            }
                        }
                    )
                }
            }
        }
    }
}