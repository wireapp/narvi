package com.wire.bots.narvi.server

import com.wire.bots.sdk.Configuration
import com.wire.bots.sdk.MessageHandlerBase
import com.wire.bots.sdk.Server
import io.dropwizard.setup.Environment

class NarviService : Server<Configuration>() {

    override fun createHandler(config: Configuration, env: Environment): MessageHandlerBase {
        return MessageHandler()
    }
}
