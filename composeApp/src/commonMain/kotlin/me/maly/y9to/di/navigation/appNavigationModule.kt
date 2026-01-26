package me.maly.y9to.di.navigation

import me.maly.y9to.screen.auth.AuthComponent
import me.maly.y9to.screen.auth.AuthComponentDefault
import me.maly.y9to.screen.feed.FeedComponent
import me.maly.y9to.screen.feed.FeedComponentDefault
import org.koin.dsl.module


val appNavigationModule = module {
    factory<AuthComponent> { AuthComponentDefault(get()) }
    factory<FeedComponent> { FeedComponentDefault(get()) }
}
