package me.maly.y9to.viewModel

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.maly.y9to.screen.feed.UiFeedItem
import me.maly.y9to.screen.feed.UiInputPostContent
import me.maly.y9to.screen.feed.UiPostPrePublishPreview
import me.maly.y9to.types.UiMyProfile


interface ViewProfileViewModel {
    val state: StateFlow<ViewProfileUiState>
    val myProfile: StateFlow<UiMyProfile?> // todo

    val pagerFlow: Flow<PagingData<UiFeedItem>>
    val prependItems: SnapshotStateList<UiFeedItem>
    val publishPreviewItems: SnapshotStateList<UiPostPrePublishPreview>
    fun publish(content: UiInputPostContent)
}
