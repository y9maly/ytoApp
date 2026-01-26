package me.maly.y9to.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import me.maly.y9to.types.UiPost
import me.maly.y9to.types.UiPostAuthorPreview
import me.maly.y9to.types.UiPostContent
import me.maly.y9to.types.UiRepostPreview
import pro.respawn.flowmvi.api.PipelineContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.reduce
import y9to.api.types.Post
import y9to.api.types.PostContent
import y9to.api.types.RepostPreview
import y9to.libs.stdlib.PagingKey
import y9to.libs.stdlib.SpliceKey
import y9to.sdk.Client
import y9to.sdk.types.FeedPagingOptions
import kotlin.properties.Delegates


interface FeedComponent {
    val header: FeedHeaderComponent
    val pagerFlow: Flow<PagingData<UiPost>>
}

private class FeedSource(private val client: Client) : PagingSource<PagingKey, UiPost>() {
    override suspend fun load(params: LoadParams<PagingKey>): LoadResult<PagingKey, UiPost> {
        return try {
            val pagingKey = params.key
            val key =
                if (pagingKey != null) SpliceKey.Continue(pagingKey)
                else SpliceKey.Initialize(FeedPagingOptions.GlobalFeed())
            delay(250)
            val result = client.feed.splice(key, params.loadSize)
            delay(250)

            LoadResult.Page(
                data = result.list.map { it.map() },
                prevKey = pagingKey,
                nextKey = result.nextPagingKey,
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<PagingKey, UiPost>): PagingKey? {
        TODO()
    }

    private fun Post.map(): UiPost {
        return UiPost(
            id = id.long.toString(),
            author = UiPostAuthorPreview.User(
                id = author.id.long.toString(),
                firstName = author.firstName,
                lastName = author.lastName,
            ),
            publishDate = publishDate,
            lastEditDate = lastEditDate,
            content = content.map(),
        )
    }

    private fun PostContent.map(): UiPostContent = when (this) {
        is PostContent.Standalone -> UiPostContent.Standalone(text)
        is PostContent.Repost -> UiPostContent.Repost(
            comment = comment,
            originalPreview = when (val preview = preview) {
                is RepostPreview.Post -> UiRepostPreview.Post(
                    id = preview.postId.long.toString(),
                    author = UiPostAuthorPreview.User(
                        id = preview.author.id.toString(),
                        firstName = preview.author.firstName,
                        lastName = preview.author.lastName,
                    ),
                    publishDate = preview.publishDate,
                    lastEditDate = preview.lastEditDate,
                    content = preview.content.map(),
                )

                is RepostPreview.DeletedPost -> UiRepostPreview.DeletedPost(
                    deletionDate = preview.deletionDate,
                    author = UiPostAuthorPreview.User(
                        id = preview.author.id.toString(),
                        firstName = preview.author.firstName,
                        lastName = preview.author.lastName,
                    ),
                )
            }
        )
    }
}

@OptIn(FlowPreview::class)
class FeedComponentDefault(private val client: Client) : ViewModel(), FeedComponent {
    override val header = FeedHeaderComponentDefault(client)

    override val pagerFlow = Pager(PagingConfig(pageSize = 5)) {
        FeedSource(client)
    }.flow.cachedIn(viewModelScope)
}
