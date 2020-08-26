package com.wire.bots.narvi.server

import com.wire.bots.sdk.Configuration
import com.wire.bots.sdk.Server
import io.dropwizard.setup.Environment
import org.jetbrains.exposed.sql.Database

class NarviService(private val handler: MessageHandler) : Server<Configuration>() {

    override fun createHandler(config: Configuration, env: Environment): MessageHandler {
        // todo refactor this to the kodein
        Database.connect(
            url = config.database.url,
            user = requireNotNull(config.database.user),
            password = requireNotNull(config.database.password),
            driver = config.database.driverClass
        )
        // todo register env in the kodein
        return handler
    }
}
