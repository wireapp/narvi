package com.wire.bots.narvi

import com.wire.bots.narvi.di.kodein
import com.wire.bots.narvi.server.NarviService
import org.kodein.di.generic.instance

fun main(args: Array<String>) {
    val service by kodein.instance<NarviService>()
    service.run(*args)
}
