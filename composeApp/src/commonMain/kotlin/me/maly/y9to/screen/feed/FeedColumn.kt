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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import me.maly.y9to.compose.components.post.PostCard
import me.maly.y9to.compose.detectMouseSecondaryGestures
import me.maly.y9to.types.UiPost
import org.jetbrains.compose.resources.painterResource
import y9to.composeapp.generated.resources.Res
import y9to.composeapp.generated.resources.more_vertical


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedColumn(
    prependPosts: List<UiPost>,
    prePublishPreviews: List<UiPostPrePublishPreview>,
    items: LazyPagingItems<UiPost>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    openDropdownMenu: ((post: UiPost, offset: Offset) -> Unit)? = null,
    gotoPostDetails: ((postId: String) -> Unit)? = null,
    listItems: LazyListScope.(items: () -> Unit) -> Unit = { it() },
) {
    val gotoPostDetails by rememberUpdatedState(gotoPostDetails)
    val openDropdownMenu by rememberUpdatedState(openDropdownMenu)

    LookaheadScope {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = contentPadding,
        ) {
            listItems {
                items(prePublishPreviews, key = { it.toString() }) { item ->
                    Box {
                        PostCard(
                            author = item.author,
                            publishDate = item.publishDate,
                            lastEditDate = null,
                            content = item.content,
                            modifier = Modifier.fillMaxWidth().animateItem(),
                        )

                        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f))) {
                            LoadingIndicator(Modifier.align(Center))
                        }
                    }
                }

                items(prependPosts.size + items.itemCount) { index ->
                    val prependItem = prependPosts.getOrNull(index)
                    val item = prependItem ?: items[index - prependPosts.size] ?: return@items
                    var positionInList by remember { mutableStateOf(Offset.Zero) }

                    PostCard(
                        post = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                positionInList = it.toLookaheadCoordinates().positionInParent()
                            }
                            .pointerInput(Unit) {
                                detectMouseSecondaryGestures { position ->
                                    val listPosition = positionInList + position
                                    openDropdownMenu?.invoke(item, listPosition)
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = { position ->
                                        val listPosition = positionInList + position
                                        openDropdownMenu?.invoke(item, listPosition)
                                    }
                                )
                            },
                        menu = {
                            var menuPosition by remember { mutableStateOf(Offset.Zero) }

                            Icon(
                                painterResource(Res.drawable.more_vertical),
                                null,
                                Modifier
                                    .onGloballyPositioned {
                                        menuPosition = it.toLookaheadCoordinates().positionInParent()
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures { position ->
                                            val listPosition = positionInList + menuPosition + position
                                            openDropdownMenu?.invoke(item, listPosition)
                                        }
                                    }
                                    .padding(4.dp)
                            )
                        },
                        onClick = { gotoPostDetails?.invoke(item.id) },
                        gotoPostDetails = gotoPostDetails
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
}
