package me.maly.y9to.compose.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner


@Composable
inline fun <reified VM> viewModel(crossinline factory: () -> VM): VM {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
        ?: error("No ViewModelStoreOwner found")
    // todo
    val provider = ViewModelProvider.create(viewModelStoreOwner.viewModelStore,)
    return remember(viewModelStoreOwner) {
        val className = VM::class.qualifiedName ?: error("ViewModel cannot be an anonymous object")
        val oldVm = viewModelStoreOwner.viewModelStore[className]
        if (oldVm != null)
            return@remember oldVm as VM
        val newVm = factory()
        if (newVm !is ViewModel)
            error("factory must return ViewModel instance, but got $newVm")
        viewModelStoreOwner.viewModelStore.put(className, newVm)
        newVm
    }
}
