package me.maly.y9to.screen.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import me.maly.y9to.compose.dropBottom
import me.maly.y9to.compose.dropTop
import me.maly.y9to.compose.plus


@Composable
fun FeedScreen(
    vm: FeedViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val headerState by vm.header.state.collectAsState()
    val lazyPagingItems = vm.pagerFlow.collectAsLazyPagingItems()

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
            lazyPagingItems,
            Modifier.fillMaxSize(),
            contentPadding = contentPadding.dropTop()
        )
    }
}