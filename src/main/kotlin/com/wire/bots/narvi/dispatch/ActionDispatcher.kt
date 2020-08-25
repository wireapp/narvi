package com.wire.bots.narvi.dispatch

interface ActionDispatcher {

    fun dispatch(actions: Collection<Action>)

    fun dispatch(action: Action)
}
