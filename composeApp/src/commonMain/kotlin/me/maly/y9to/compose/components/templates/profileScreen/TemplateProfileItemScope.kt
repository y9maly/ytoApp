package me.maly.y9to.compose.components.templates.profileScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.LookaheadScope
import me.maly.y9to.compose.components.templates.profileScreen.TemplateProfileItemScope.Defaults


interface TemplateProfileItemScope : LookaheadScope {
    object Defaults

    fun item(key: Any, content: @Composable Defaults.() -> Unit)
}

internal class TemplateProfileItemScopeImpl(
    lookaheadScope: LookaheadScope
) : TemplateProfileItemScope, LookaheadScope by lookaheadScope {
    val items = mutableListOf<Pair<Any, @Composable () -> Unit>>()

    override fun item(key: Any, content: @Composable Defaults.() -> Unit) {
        items += key to { content(Defaults) }
    }
}
