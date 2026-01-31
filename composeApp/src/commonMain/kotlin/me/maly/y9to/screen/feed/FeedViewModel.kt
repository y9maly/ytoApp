package me.maly.y9to.screen.feed

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.maly.y9to.types.UiMyProfile
import me.maly.y9to.types.UiPost


interface FeedViewModel {
    val profile: Flow<UiMyProfile?> // null if unauthenticated
    val header: FeedHeaderComponent
    val pagerFlow: Flow<PagingData<UiPost>>
    val prependPosts: SnapshotStateList<UiPost>

    val prePublishPreviews: SnapshotStateList<UiPostPrePublishPreview>

    fun publish(content: UiInputPostContent)
}

interface FeedHeaderComponent {
    val state: StateFlow<FeedHeaderUiState>
}
