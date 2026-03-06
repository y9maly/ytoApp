package me.maly.y9to.repository

import androidx.paging.Pager
import y9to.api.types.Post
import y9to.api.types.UserId
import y9to.libs.paging.Cursor


interface ProfileFeedRepository {
    fun createPager(user: UserId): Pager<Cursor, Post>
}
