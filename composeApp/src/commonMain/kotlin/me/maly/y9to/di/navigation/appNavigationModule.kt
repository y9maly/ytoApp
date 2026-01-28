package me.maly.y9to.di.navigation

import me.maly.y9to.screen.auth.AuthDefaultViewModel
import me.maly.y9to.screen.auth.AuthViewModel
import me.maly.y9to.screen.feed.FeedDefaultViewModel
import me.maly.y9to.screen.feed.FeedViewModel
import me.maly.y9to.screen.mainFlow.MainFlowDefaultViewModel
import me.maly.y9to.screen.mainFlow.MainFlowViewModel
import me.maly.y9to.screen.myProfile.MyProfileDefaultViewModel
import me.maly.y9to.screen.myProfile.MyProfileViewModel
import org.koin.dsl.module


val appNavigationModule = module {
    factory<AuthViewModel> { AuthDefaultViewModel(get()) }
    factory<FeedViewModel> { FeedDefaultViewModel(get()) }
    factory<MyProfileViewModel> { MyProfileDefaultViewModel(get()) }
    factory<MainFlowViewModel> { MainFlowDefaultViewModel(get()) }
}
