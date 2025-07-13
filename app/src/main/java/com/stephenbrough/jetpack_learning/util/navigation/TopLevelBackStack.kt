package com.stephenbrough.jetpack_learning.util.navigation

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
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
     *   Settings -> [Settings]
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
                remove(key)
                put(key, this[key]!!)
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
     * Clears entire back stack. Typically only used when logging in
     * or out
     *
     * @param startKey The new top-level route
     */
    fun clear(startKey: T) {
        topLevelStacks.clear()
        topLevelStacks.put(startKey, mutableStateListOf(startKey))
        topLevelKey = startKey
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

    internal fun restoreState(
        stacks: LinkedHashMap<T, SnapshotStateList<T>>,
        topLevelKey: T,
    ) {
        topLevelStacks = stacks
        this.topLevelKey = topLevelKey
        updateBackStack()
    }
}


/**
 * This is to allow the use of rememberSaveable for this nav stack
 */

private const val TOP_LEVEL_ROUTE_KEY = "topLevelKey"
private const val TOP_LEVEL_STACKS_KEY = "topLevelStacks"
private const val TOP_LEVEL_KEY_ORDER_KEY = "topLevelKeyOrder"
fun topLevelSaver() = Saver<TopLevelBackStack<Any>, Bundle>(
    save = { backStack ->
        Bundle().apply {
            // Save the current top-level route
            putString(TOP_LEVEL_ROUTE_KEY, RouteRegistry.toName(backStack.topLevelKey))

            // Save the structure of the stacks
            val stacksBundle = Bundle()
            backStack.topLevelStacks.forEach { (key, stack) ->
                val keyName = RouteRegistry.toName(key)
                val stackArray = stack.map { RouteRegistry.toName(it) }.toTypedArray()
                stacksBundle.putStringArray(keyName, stackArray)
            }
            putBundle(TOP_LEVEL_STACKS_KEY, stacksBundle)

            // Save order of top-level keys (needed due to LinkedHashMap)
            val keyOrder = backStack.topLevelStacks.keys.mapNotNull { RouteRegistry.toName(it) }.toTypedArray()
            putStringArray(TOP_LEVEL_KEY_ORDER_KEY, keyOrder)
        }

    },
    restore = { bundle ->
        try {
        val topLevelKeyName = bundle.getString(TOP_LEVEL_ROUTE_KEY) ?: return@Saver null
            val topLevelKey = RouteRegistry.fromName(topLevelKeyName) ?: return@Saver null

        val stacksBundle = bundle.getBundle(TOP_LEVEL_STACKS_KEY) ?: return@Saver null
        val keyOrder = bundle.getStringArray(TOP_LEVEL_KEY_ORDER_KEY) ?: return@Saver null

            val restoredStacks = linkedMapOf<Any, SnapshotStateList<Any>>()

            keyOrder.forEach { keyName ->
                val key = RouteRegistry.fromName(keyName) ?: return@forEach
                val stackArray = stacksBundle.getStringArray(keyName) ?: return@forEach

                val stack = mutableStateListOf<Any>()
                stackArray.forEach {
                    RouteRegistry.fromName(it)?.let { stack.add(it) }
                }
                restoredStacks[key] = stack
            }

            TopLevelBackStack<Any>(topLevelKey).apply {
                restoreState(restoredStacks, topLevelKey)
            }

        } catch (e: Exception) {
            null
        }
    }
)

object RouteRegistry {
    private val routes = mapOf(
        "Loading" to Loading,
        "Login" to Login,
        "Landing" to Landing,
        "Settings" to Settings,
        "HarryPotterList" to HarryPotterList,
        "HarryPotterDetail" to HarryPotterDetail,
        "AmiiboList" to AmiiboList,
        "AmiigoDetail" to AmiigoDetail
    )

    fun fromName(name: String): Any? = routes[name]
    fun toName(route: Any): String? = route::class.simpleName
}
