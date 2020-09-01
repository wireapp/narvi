package com.wire.bots.narvi.server

import com.wire.bots.narvi.di.USER_ID
import com.wire.bots.narvi.di.buildDiContainer
import com.wire.bots.sdk.Configuration
import com.wire.bots.sdk.Server
import com.wire.bots.sdk.user.LoginClient
import io.dropwizard.setup.Environment
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import java.util.UUID

/**
 * Dropwizard main service.
 */
class NarviService : Server<Configuration>() {
    // initialized in createRepo, can be used in createHandler
    private lateinit var userId: UUID

    override fun createHandler(config: Configuration, env: Environment): MessageHandler {
        val kodein = buildDiContainer(config, env)
        val handler by kodein.instance<MessageHandler>()
        return handler
    }

    override fun createClientRepo(): NarviClientRepo {
        val access = LoginClient(client)
            .login(config.userMode.email, config.userMode.password)
        userId = access.userId

        return NarviClientRepo(
            client,
            cryptoFactory,
            storageFactory,
            userId
        )
    }

    private fun buildDiContainer(config: Configuration, env: Environment): Kodein {
        // verify that super.repo is product of createClientRepo
        val repo = super.repo as NarviClientRepo
        // register this stuff in the Kodein
        return buildDiContainer {
            // bind parameter stuff
            bind() from singleton { env }
            bind() from singleton { config }
            bind() from singleton { client }
            bind() from singleton { cryptoFactory }
            bind() from singleton { storageFactory }
            bind() from singleton { repo }
            constant(USER_ID) with singleton { userId }
        }
    }
}
