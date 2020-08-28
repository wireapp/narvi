package com.wire.bots.narvi

import com.wire.bots.narvi.di.diContainer
import com.wire.bots.narvi.server.NarviService
import org.kodein.di.generic.instance

fun main(args: Array<String>) {
    val service by diContainer.instance<NarviService>()
    service.run(*args)
}
