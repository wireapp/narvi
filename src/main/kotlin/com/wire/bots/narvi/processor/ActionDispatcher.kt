package com.wire.bots.narvi.processor

interface ActionDispatcher {

    fun dispatch(actions: Collection<Action>) = actions.forEach { dispatch(it) }

    fun dispatch(action: Action)
}
