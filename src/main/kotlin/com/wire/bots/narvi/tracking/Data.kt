package com.wire.bots.narvi.tracking

import java.net.URL

data class CreateIssueRequest(
    val repository: String,
    val title: String,
    val body: String
)

data class AddCommentRequest(
    val repository: String,
    val issueId: String,
    val comment: String
)

data class CreatedResource(
    val id: String,
    val link: URL
)
