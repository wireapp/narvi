package com.wire.bots.narvi.di

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.db.TemplatesService
import com.wire.bots.narvi.dispatch.ActionDispatcher
import com.wire.bots.narvi.dispatch.SynchronousActionDispatcher
import com.wire.bots.narvi.processor.CommandsProcessor
import com.wire.bots.narvi.processor.DummyCommandsProcessor
import com.wire.bots.narvi.server.MessageHandler
import com.wire.bots.narvi.tracking.AggregatingIssueTracker
import com.wire.bots.narvi.tracking.IssueTracker
import com.wire.bots.narvi.tracking.github.GithubIssueTracker
import com.wire.bots.sdk.Configuration
import io.dropwizard.setup.Environment
import org.jetbrains.exposed.sql.Database
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import org.kohsuke.github.GitHubBuilder
import pw.forst.tools.katlib.getEnv
import pw.forst.tools.katlib.propertiesFromResources
import java.util.Properties

const val GITHUB_TOKEN = "github-token"


fun buildDiContainer(config: Configuration, env: Environment) =
    Kodein {
        val props by lazy { propertiesFromResources("/secret.properties") ?: Properties() }

        // bind configuration
        bind() from singleton { env }
        bind() from singleton { config }
        constant(GITHUB_TOKEN) with (getEnv("GITHUB_TOKEN") ?: props.getProperty("github.token"))

        // database
        bind() from singleton {
            instance<Configuration>().let { config ->
                Database.connect(
                    url = config.database.url,
                    user = requireNotNull(config.database.user) { "Database user was null!" },
                    password = requireNotNull(config.database.password) { "Database password was null!" },
                    driver = config.database.driverClass
                )
            }
        }
        bind() from singleton { IssuesService(instance()) }
        bind() from singleton { TemplatesService(instance()) }

        // server
        bind() from singleton { MessageHandler(instance(), instance()) }

        // tracking
        bind() from singleton {
            GitHubBuilder()
                .withOAuthToken(instance(GITHUB_TOKEN))
                .build()
        }
        bind() from singleton { GithubIssueTracker(instance()) }
        bind() from singleton { AggregatingIssueTracker(instance()) }
        // select default tracking system
        bind<IssueTracker>() with singleton { instance<AggregatingIssueTracker>() }

        // dispatcher
        bind() from singleton { SynchronousActionDispatcher(instance(), instance(), instance()) }
        bind<ActionDispatcher>() with singleton { instance<SynchronousActionDispatcher>() }
        // command processor
        bind() from singleton { DummyCommandsProcessor(instance(), instance()) }
        bind<CommandsProcessor>() with singleton { instance<DummyCommandsProcessor>() }
    }
