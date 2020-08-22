package com.wire.bots.narvi.di

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

const val GITHUB_TOKEN = "github-token"

val configurationDi = Kodein.Module("ConfigurationModule") {
    bind(GITHUB_TOKEN) from singleton { "SOME_TOKEN" }
}
