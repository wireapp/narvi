package com.wire.bots.narvi.dispatch

/**
 * Dispatcher which performs given action.
 * Part of the actor model of the application.
 */
interface ActionDispatcher {

    /**
     * Dispatch multiple actions.
     */
    fun dispatch(actions: Collection<Action>)

    /**
     * Dispatch multiple actions.
     */
    fun dispatch(action: Action)
}
