package com.wire.bots.narvi.di

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import pw.forst.tools.katlib.getEnv
import pw.forst.tools.katlib.propertiesFromResources
import java.util.Properties

const val GITHUB_TOKEN = "github-token"

val configurationDi = Kodein.Module("ConfigurationModule") {
    val props by lazy { propertiesFromResources("/secret.properties") ?: Properties() }

    bind(GITHUB_TOKEN) from singleton {
        getEnv("GITHUB_TOKEN") ?: props.getProperty("github.token")
    }
}
