package com.wire.bots.narvi

import com.wire.bots.narvi.server.NarviService
import mu.KLogging

fun main(args: Array<String>) {
    KLogging().logger.info { "Hello" }
    NarviService().run(*args)
}
