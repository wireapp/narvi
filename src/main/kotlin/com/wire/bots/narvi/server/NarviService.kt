package com.wire.bots.narvi.server

import com.wire.bots.sdk.Configuration
import com.wire.bots.sdk.Server
import io.dropwizard.setup.Environment
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class NarviService(private val kodein: Kodein) : Server<Configuration>() {

    override fun createHandler(config: Configuration, env: Environment): MessageHandler {
        // register this stuff in the Kodein
        Kodein {
            extend(kodein)
            bind() from singleton { env }
            bind() from singleton { config }
        }

        val handler by kodein.instance<MessageHandler>()
        return handler
    }
}
