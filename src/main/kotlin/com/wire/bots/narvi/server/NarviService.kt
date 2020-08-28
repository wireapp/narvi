package com.wire.bots.narvi.server

import com.wire.bots.narvi.di.buildDiContainer
import com.wire.bots.sdk.Configuration
import com.wire.bots.sdk.Server
import io.dropwizard.setup.Environment
import org.kodein.di.generic.instance

class NarviService : Server<Configuration>() {

    override fun createHandler(config: Configuration, env: Environment): MessageHandler {
        // register this stuff in the Kodein
        val kodein = buildDiContainer(config, env)
        val handler by kodein.instance<MessageHandler>()
        return handler
    }
}
