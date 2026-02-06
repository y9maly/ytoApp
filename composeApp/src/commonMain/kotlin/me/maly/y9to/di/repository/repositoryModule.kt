package me.maly.y9to.di.repository

import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.TimeZone
import me.maly.y9to.repository.AuthInfoRepository
import me.maly.y9to.repository.AuthInfoRepositoryDefault
import me.maly.y9to.repository.AuthRepository
import me.maly.y9to.repository.AuthRepositoryDefault
import me.maly.y9to.repository.FeedRepository
import me.maly.y9to.repository.FeedRepositoryDefault
import me.maly.y9to.repository.MyProfileRepository
import me.maly.y9to.repository.MyProfileRepositoryDefault
import me.maly.y9to.repository.CreatePostRepository
import me.maly.y9to.repository.CreatePostRepositoryDefault
import me.maly.y9to.repository.ViewPostRepository
import me.maly.y9to.repository.ViewPostRepositoryDefault
import org.koin.dsl.module
import kotlin.time.Clock


val repositoryModule = module {
    single<MyProfileRepository> {
        MyProfileRepositoryDefault(
            scope = GlobalScope, // todo ApplicationScope idk
            client = get(),
            timeZone = TimeZone.currentSystemDefault(),
            clock = Clock.System,
        )
    }

    single<FeedRepository> {
        FeedRepositoryDefault(get())
    }

    single<ViewPostRepository> {
        ViewPostRepositoryDefault(get())
    }

    single<CreatePostRepository> {
        CreatePostRepositoryDefault(get())
    }

    single<AuthInfoRepository> {
        AuthInfoRepositoryDefault(get())
    }

    single<AuthRepository> {
        AuthRepositoryDefault(get())
    }
}
