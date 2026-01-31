package me.maly.y9to.di.navigation

import me.maly.y9to.screen.auth.AuthDefaultViewModel
import me.maly.y9to.screen.auth.AuthViewModel
import me.maly.y9to.screen.feed.FeedDefaultViewModel
import me.maly.y9to.screen.feed.FeedViewModel
import me.maly.y9to.screen.mainFlow.MainFlowDefaultViewModel
import me.maly.y9to.screen.mainFlow.MainFlowViewModel
import me.maly.y9to.screen.myProfile.MyProfileDefaultViewModel
import me.maly.y9to.screen.myProfile.MyProfileViewModel
import me.maly.y9to.screen.navigation.NavigationDefaultViewModel
import me.maly.y9to.screen.navigation.NavigationViewModel
import me.maly.y9to.screen.postDetails.PostDetailsDefaultViewModel
import me.maly.y9to.screen.postDetails.PostDetailsViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.module
import y9to.api.types.PostId


private inline fun <reified T> Module.vm(crossinline definition: Definition<T>): KoinDefinition<T> {
    return factory {
        definition(this, it)
    }
}

val appNavigationModule = module {
    vm<NavigationViewModel> { NavigationDefaultViewModel(get()) }
    vm<AuthViewModel> { AuthDefaultViewModel(get()) }
    vm<FeedViewModel> { FeedDefaultViewModel(get()) }
    vm<MyProfileViewModel> { MyProfileDefaultViewModel(get()) }
    vm<MainFlowViewModel> { MainFlowDefaultViewModel(get()) }
    vm<PostDetailsViewModel> { params ->
        PostDetailsDefaultViewModel(
            client = get(),
            postId = PostId(params.get<String>(0).toLong())
        )
    }
}
