package com.wire.bots.narvi.server

import com.wire.bots.sdk.Configuration
import com.wire.bots.sdk.Server
import io.dropwizard.setup.Environment

class NarviService(private val handler: MessageHandler) : Server<Configuration>() {

    override fun createHandler(config: Configuration, env: Environment) = handler
}
