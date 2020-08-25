package com.wire.bots.narvi.tracking

import com.wire.bots.narvi.db.model.IssueTracker
import java.net.URL

sealed class TrackingRequest

data class CreateIssueRequest(
    val repository: String,
    val title: String,
    val body: String,
    val issueTracker: IssueTracker
) : TrackingRequest()

data class AddCommentRequest(
    val repository: String,
    val issueId: String,
    val comment: String,
    val issueTracker: IssueTracker
) : TrackingRequest()

data class CloseIssueRequest(
    val repository: String,
    val issueId: String,
    val issueTracker: IssueTracker
) : TrackingRequest()

data class CreatedResource(
    val id: String,
    val link: URL
)
