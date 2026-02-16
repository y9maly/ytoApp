package me.maly.y9to.di.navigation

import me.maly.y9to.viewModel.AuthDefaultViewModel
import me.maly.y9to.viewModel.AuthViewModel
import me.maly.y9to.viewModel.FeedDefaultViewModel
import me.maly.y9to.viewModel.FeedViewModel
import me.maly.y9to.viewModel.MainFlowDefaultViewModel
import me.maly.y9to.viewModel.MainFlowViewModel
import me.maly.y9to.viewModel.MyProfileDefaultViewModel
import me.maly.y9to.viewModel.MyProfileViewModel
import me.maly.y9to.viewModel.NavigationDefaultViewModel
import me.maly.y9to.viewModel.NavigationViewModel
import me.maly.y9to.viewModel.PostDetailsDefaultViewModel
import me.maly.y9to.viewModel.PostDetailsViewModel
import me.maly.y9to.viewModel.ViewProfileDefaultViewModel
import me.maly.y9to.viewModel.ViewProfileViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


private inline fun <reified T> Module.vm(crossinline definition: Definition<T>): KoinDefinition<T> {
    return factory {
        definition(this, it)
    }
}

val appNavigationModule = module {
    vm<NavigationViewModel> { NavigationDefaultViewModel(get()) }
    vm<AuthViewModel> { AuthDefaultViewModel(get(), get()) }
    vm<FeedViewModel> { FeedDefaultViewModel(get(), get(), get(), get(), get()) }
    vm<MyProfileViewModel> { MyProfileDefaultViewModel(get()) }
    vm<MainFlowViewModel> { MainFlowDefaultViewModel(get()) }
    vm<PostDetailsViewModel> { params ->
        PostDetailsDefaultViewModel(
            uiPostId = params[0],
            viewPostRepository = get(),
        )
    }
    vm<ViewProfileViewModel> { params ->
        ViewProfileDefaultViewModel(
            uiUserId = params[0],
            repository = get(),
        )
    }
}
