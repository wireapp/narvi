package com.wire.bots.narvi.dispatch

interface ActionDispatcher {

    fun dispatch(actions: Collection<Action>) = actions.forEach { dispatch(it) }

    fun dispatch(action: Action)
}
