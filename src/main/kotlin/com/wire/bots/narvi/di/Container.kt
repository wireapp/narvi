package com.wire.bots.narvi.di

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.db.UsernamesService
import com.wire.bots.narvi.dispatch.ActionDispatcher
import com.wire.bots.narvi.dispatch.SynchronousActionDispatcher
import com.wire.bots.narvi.processor.CommandsProcessor
import com.wire.bots.narvi.processor.DummyCommandsProcessor
import com.wire.bots.narvi.server.MessageHandler
import com.wire.bots.narvi.server.NarviService
import com.wire.bots.narvi.tracking.AggregatingIssueTracker
import com.wire.bots.narvi.tracking.IssueTracker
import com.wire.bots.narvi.tracking.github.GithubIssueTracker
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kohsuke.github.GitHubBuilder

val kodein = Kodein {
    import(configurationDi)

    // database
    bind() from singleton { IssuesService() }
    bind() from singleton { UsernamesService() }

    // server
    bind() from singleton { MessageHandler(instance(), instance()) }
    bind<NarviService>() with singleton { NarviService(instance()) }

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
    bind() from singleton { SynchronousActionDispatcher(instance()) }
    bind<ActionDispatcher>() with singleton { instance<SynchronousActionDispatcher>() }
    // command processor
    bind() from singleton { DummyCommandsProcessor() }
    bind<CommandsProcessor>() with singleton { instance<DummyCommandsProcessor>() }
}
