package me.maly.y9to.di.navigation

import me.maly.y9to.viewModel.AuthDefaultViewModel
import me.maly.y9to.viewModel.AuthViewModel
import me.maly.y9to.viewModel.FeedDefaultViewModel
import me.maly.y9to.viewModel.FeedViewModel
import me.maly.y9to.viewModel.MainFlowViewModelDefault
import me.maly.y9to.viewModel.MainFlowViewModel
import me.maly.y9to.viewModel.MyProfileDefaultViewModel
import me.maly.y9to.viewModel.MyProfileViewModel
import me.maly.y9to.viewModel.NavigationViewModelDefault
import me.maly.y9to.viewModel.NavigationViewModel
import me.maly.y9to.viewModel.PostDetailsViewModelDefault
import me.maly.y9to.viewModel.PostDetailsViewModel
import me.maly.y9to.viewModel.ViewProfileViewModelDefault
import me.maly.y9to.viewModel.ViewProfileViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.module


private inline fun <reified T> Module.vm(crossinline definition: Definition<T>): KoinDefinition<T> {
    return factory {
        definition(this, it)
    }
}

val appNavigationModule = module {
    vm<NavigationViewModel> { NavigationViewModelDefault(get()) }
    vm<AuthViewModel> { AuthDefaultViewModel(get(), get()) }
    vm<FeedViewModel> { FeedDefaultViewModel(get(), get(), get(), get(), get()) }
    vm<MyProfileViewModel> { MyProfileDefaultViewModel(get()) }
    vm<MainFlowViewModel> { MainFlowViewModelDefault(get()) }
    vm<PostDetailsViewModel> { params ->
        PostDetailsViewModelDefault(
            uiPostId = params[0],
            viewPostRepository = get(),
        )
    }
    vm<ViewProfileViewModel> { params ->
        ViewProfileViewModelDefault(
            uiUserId = params[0],
            repository = get(),
            myProfileRepository = get(),
            feedRepository = get(),
            createPostRepository = get(),
        )
    }
}
