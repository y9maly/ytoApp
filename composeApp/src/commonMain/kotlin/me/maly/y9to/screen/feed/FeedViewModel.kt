package me.maly.y9to.screen.feed

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.maly.y9to.types.UiPost


interface FeedViewModel {
    val header: FeedHeaderComponent
    val pagerFlow: Flow<PagingData<UiPost>>
}

interface FeedHeaderComponent {
    val state: StateFlow<FeedHeaderState>
}
