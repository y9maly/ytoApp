package me.maly.y9to.screen.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import me.maly.y9to.compose.components.post.PostCard
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.types.UiPost


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedColumn(
    newPosts: List<UiPost>,
    prePublishPreviews: List<UiPostPrePublishPreview>,
    items: LazyPagingItems<UiPost>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    listItems: LazyListScope.(items: () -> Unit) -> Unit = { it() },
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        listItems {
            items(prePublishPreviews) { item ->
                Box {
                    PostCard(
                        author = item.author,
                        publishDate = item.publishDate,
                        lastEditDate = null,
                        content = item.content,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f))) {
                        LoadingIndicator(Modifier.align(Center))
                    }
                }
            }

            items(newPosts) { item ->
                PostCard(item, Modifier.fillMaxWidth())
            }

            items(items.itemCount) { index ->
                val item = items[index] ?: return@items
                PostCard(item, Modifier.fillMaxWidth())
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
