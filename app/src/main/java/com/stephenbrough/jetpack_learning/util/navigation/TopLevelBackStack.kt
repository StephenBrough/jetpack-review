package com.stephenbrough.jetpack_learning.util.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Custom back stack handling. Allows for having top-level routes
 * with their own back stack (e.g. List views -> Detail views)
 *
 * [topLevelKey] is used to determine which top-level route is currently active
 * [backStack] is the backstack for the currently active top-level route; pass
 * this to a [androidx.navigation3.ui.NavDisplay] to give it a backstack
 *
 * @param startKey The initial top-level route for the app
 *
 *
 */
class TopLevelBackStack<T : Any>(startKey: T) {

    /**
     * Each top-level route will maintain their own stack
     *
     * Will look something like this as routes are added:
     *
     * topLevelStacks = {
     *   HarryPotter -> [HarryPotter, HarryPotterDetails],
     *   Amiibo -> [Amiibo, AmiiboDetails]
     * }
     */
    internal var topLevelStacks: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    // The current top-level route
    var topLevelKey by mutableStateOf(startKey)
        private set

    // Get the backstack for the NavDisplay
    val backStack = mutableStateListOf(startKey)

    // Clears the backstack and then flattens the top-level stacks
    // into a single list
    private fun updateBackStack() = backStack.apply {
        clear()
        addAll(topLevelStacks.flatMap { it.value })
    }

    fun addTopLevel(key: T) {
        // If the top level for this key doesn't exist, add it
        if (topLevelStacks[key] == null) {
            topLevelStacks.put(key, mutableStateListOf(key))
        } else {
            // Otherwise move it to the end of the stacks
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    /**
     * Add a child route to the top-level route
     */
    fun add(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    /**
     * Removes the last route. If the last route was a top-level route
     * then remove any child routes as well
     */
    fun removeLast() {
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If we removed a top-level route then remove any child routes as well
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }

    companion object {
        // Saver for rememberSaveable
        fun Saver(): Saver<TopLevelBackStack<Any>, Any> = listSaver(
            save = { backStack ->
                listOf(backStack.topLevelKey, backStack.topLevelStacks)
            },
            restore = { list ->
                TopLevelBackStack(list[0]).apply {
                    topLevelStacks = list[1] as LinkedHashMap<Any, SnapshotStateList<Any>>
                    updateBackStack()
                }
            }
        )
    }
}

