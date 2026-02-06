package me.maly.y9to.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import y9to.api.types.Post
import y9to.libs.stdlib.PagingKey
import y9to.libs.stdlib.SpliceKey
import y9to.sdk.Client
import y9to.sdk.types.FeedPagingOptions


class FeedRepositoryDefault(private val client: Client) : FeedRepository {
    override fun createPager(): Pager<PagingKey, Post> {
        return Pager(PagingConfig(pageSize = 5)) {
            FeedSource(client)
        }
    }
}

private class FeedSource(private val client: Client) : PagingSource<PagingKey, Post>() {
    override suspend fun load(params: LoadParams<PagingKey>): LoadResult<PagingKey, Post> {
        return try {
            val pagingKey = params.key
            val key =
                if (pagingKey != null) SpliceKey.Continue(pagingKey)
                else SpliceKey.Initialize(FeedPagingOptions.GlobalFeed())
            delay(250)
            val result = client.feed.splice(key, params.loadSize)
            delay(250)

            LoadResult.Page(
                data = result.list,
                prevKey = pagingKey,
                nextKey = result.nextPagingKey,
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<PagingKey, Post>): PagingKey? {
        TODO()
    }
}
