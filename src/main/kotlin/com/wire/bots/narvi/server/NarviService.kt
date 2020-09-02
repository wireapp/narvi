package com.wire.bots.narvi.server

import com.wire.bots.narvi.di.USER_ID
import com.wire.bots.narvi.di.buildDiContainer
import com.wire.bots.narvi.server.resources.GitHubResource
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
    private lateinit var kodein: Kodein

    override fun createHandler(config: Configuration, env: Environment): MessageHandler {
        val handler by kodein.instance<MessageHandler>()
        return handler
    }

    override fun initialize(config: Configuration, env: Environment) {
        super.initialize(config, env)
        // called after createClientRepo, kodein is initialized
        val githubResource by kodein.instance<GitHubResource>()
        addResource(githubResource)
    }

    override fun createClientRepo(): NarviClientRepo {
        val access = LoginClient(client)
            .login(config.userMode.email, config.userMode.password)

        return NarviClientRepo(
            client,
            cryptoFactory,
            storageFactory,
            access.userId
        ).also {
            // this is ugly, but we finally have everything to build DI container
            kodein = buildDiContainer(access.userId, it)
        }
    }

    private fun buildDiContainer(userId: UUID, repo: NarviClientRepo): Kodein {
        // register this stuff in the Kodein
        return buildDiContainer {
            // bind parameter stuff
            bind() from singleton { environment }
            bind() from singleton { config }
            bind() from singleton { client }
            bind() from singleton { cryptoFactory }
            bind() from singleton { storageFactory }
            bind() from singleton { repo }
            constant(USER_ID) with singleton { userId }
        }
    }
}
