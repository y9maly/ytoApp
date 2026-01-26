package me.maly.y9to.screen.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import me.maly.y9to.compose.components.post.Post
import me.maly.y9to.compose.components.post.PostCard
import me.maly.y9to.compose.plus
import me.maly.y9to.compose.plusAll
import pro.respawn.flowmvi.compose.dsl.subscribe


@Composable
fun FeedScreen(
    component: FeedComponent,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val headerState by component.header.state.collectAsState()
    val pagerFlow = component.pagerFlow

    Column(modifier) {
        FeedHeader(
            modifier = Modifier.fillMaxWidth(),
            state = headerState,
            contentPadding = contentPadding.plus(vertical = 8.dp)
        )

        FeedColumn(pagerFlow, Modifier.fillMaxSize())
    }
}