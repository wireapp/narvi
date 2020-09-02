package com.wire.bots.narvi.server.resources

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    @JsonProperty
    val login: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Issue(
    @JsonProperty("html_url")
    val htmlUrl: String,

    @JsonProperty
    val title: String,

    @JsonProperty
    val body: String,

    @JsonProperty
    val user: User,

    @JsonProperty
    val number: Int
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class Comment(
    @JsonProperty
    val body: String,

    @JsonProperty
    val user: User,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitWebhook(
    @JsonProperty
    val action: String? = null,

    @JsonProperty
    val comment: Comment? = null,

    @JsonProperty
    val issue: Issue? = null,

    @JsonProperty
    val sender: User? = null,

    @JsonProperty
    val compare: String? = null,

    @JsonProperty
    val repository: Repository? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    @JsonProperty("full_name")
    val fullName: String,

    @JsonProperty
    val name: String
)

