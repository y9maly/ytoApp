package me.maly.y9to.screen.feed

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import me.maly.y9to.compose.components.post.PostCard
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.types.UiPost


@Composable
fun FeedColumn(
    pagerFlow: Flow<PagingData<UiPost>>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val items = pagerFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier
            .padding(contentPadding.dropBottom()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                        Box(
                            Modifier.fillParentMaxWidth().height(60.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.endOfPaginationReached) {
                                Text("That's all.")
                            } else {
                                CircularProgressIndicator(Modifier.size(30.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
