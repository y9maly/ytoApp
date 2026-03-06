package me.maly.y9to.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import y9to.api.types.InputFeed
import y9to.api.types.Post
import y9to.libs.paging.Cursor
import y9to.libs.paging.SliceKey
import y9to.sdk.Client


class GlobalFeedRepositoryDefault(private val client: Client) : GlobalFeedRepository {
    override fun createPager(): Pager<Cursor, Post> {
        return Pager(PagingConfig(pageSize = 5)) {
            GlobalFeedSource(client)
        }
    }
}

private class GlobalFeedSource(private val client: Client) : PagingSource<Cursor, Post>() {
    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, Post> {
        return try {
            val pagingKey = params.key
            val key =
                if (pagingKey != null) SliceKey.Next(pagingKey)
                else SliceKey.Initialize(InputFeed.Global)
            delay(250)
            val result = client.post.sliceFeed(key, params.loadSize)
            delay(250)

            LoadResult.Page(
                data = result.items,
                prevKey = pagingKey,
                nextKey = result.nextCursor,
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Cursor, Post>): Cursor? {
        TODO()
    }
}
