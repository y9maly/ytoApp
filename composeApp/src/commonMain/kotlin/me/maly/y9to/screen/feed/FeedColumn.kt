package me.maly.y9to.screen.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import me.maly.y9to.compose.components.post.PostCard
import me.maly.y9to.compose.utils.detectMouseSecondaryGestures
import me.maly.y9to.types.UiPost
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.more_vertical


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedColumn(
    publishPreviewItems: List<UiPostPrePublishPreview>,
    prependItems: List<UiFeedItem>,
    items: LazyPagingItems<UiFeedItem>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    openDropdownMenu: ((item: UiFeedItem, offset: Offset) -> Unit)? = null,
    gotoPostDetails: ((postId: String) -> Unit)? = null,
    gotoAuthorProfile: ((authorId: String) -> Unit)? = null,
    listItems: LazyListScope.(items: () -> Unit) -> Unit = { it() },
) {
    val gotoPostDetails by rememberUpdatedState(gotoPostDetails)
    val gotoAuthorProfile by rememberUpdatedState(gotoAuthorProfile)
    val openDropdownMenu by rememberUpdatedState(openDropdownMenu)

    var listCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    LazyColumn(
        modifier = modifier
            .onGloballyPositioned { listCoordinates = it },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        listItems {
            items(publishPreviewItems, key = { it.toString() }) { item ->
                PostCard(
                    author = item.author,
                    publishDate = item.publishDate,
                    lastEditDate = null,
                    content = item.content,
                    modifier = Modifier.fillMaxWidth().animateItem(),
                    gotoAuthorProfile = gotoAuthorProfile,
                    overlay = {
                        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f))) {
                            LoadingIndicator(Modifier.align(Center))
                        }
                    }
                )
            }

            items(prependItems.size + items.itemCount) { index ->
                val prependItem = prependItems.getOrNull(index)
                val item = prependItem ?: items[index - prependItems.size] ?: return@items

                var itemCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
                val itemOffset by remember {
                    derivedStateOf {
                        val listCoordinates = listCoordinates
                            ?: return@derivedStateOf Offset.Zero
                        val itemCoordinates = itemCoordinates
                            ?: return@derivedStateOf Offset.Zero
                        listCoordinates.localPositionOf(itemCoordinates)
                    }
                }

                var menuCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
                val menuOffset by remember {
                    derivedStateOf {
                        val listCoordinates = listCoordinates
                            ?: return@derivedStateOf Offset.Zero
                        val menuCoordinates = menuCoordinates
                            ?: return@derivedStateOf Offset.Zero
                        listCoordinates.localPositionOf(menuCoordinates)
                    }
                }

                PostCard(
                    post = item.post,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { itemCoordinates = it }
                        .pointerInput(Unit) {
                            detectMouseSecondaryGestures(
                                onPress = { position ->
                                    openDropdownMenu?.invoke(item, itemOffset + position)
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { position ->
                                    openDropdownMenu?.invoke(item, itemOffset + position)
                                }
                            )
                        },
                    menu = {
                        Icon(
                            painterResource(Res.drawable.more_vertical),
                            null,
                            Modifier
                                .onGloballyPositioned { menuCoordinates = it }
                                .pointerInput(Unit) {
                                    detectTapGestures { position ->
                                        openDropdownMenu?.invoke(item, menuOffset + position)
                                    }
                                }
                                .padding(4.dp)
                        )
                    },
                    onClick = { gotoPostDetails?.invoke(item.post.id) },
                    gotoPostDetails = gotoPostDetails,
                    gotoAuthorProfile = gotoAuthorProfile,
                )
            }

            item {
                Column(
                    Modifier.fillParentMaxWidth()
                        .padding(vertical = 8.dp)
                        .heightIn(min = 60.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (val state = items.loadState.append) {
                        is LoadState.Error -> {
                            Text("Oops, something went wrong...")
                            Spacer(Modifier.height(8.dp))
                            Button({ items.retry() }) { Text("Retry") }
                        }

                        is LoadState.Loading,
                        is LoadState.NotLoading -> {
                            AnimatedContent(
                                state.endOfPaginationReached,
                                transitionSpec = {
                                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                                }
                            ) { endOfPaginationReached ->
                                Box(
                                    Modifier.fillParentMaxWidth().height(60.dp),
                                    contentAlignment = Center,
                                ) {
                                    if (endOfPaginationReached) {
                                        Text("That's all.")
                                    } else {
                                        LoadingIndicator(Modifier.size((60 * 2/3).dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
