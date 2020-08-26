package com.wire.bots.narvi.tracking

import com.wire.bots.narvi.db.model.IssueTracker
import java.net.URL
import java.util.UUID

sealed class TrackingRequest {
    abstract val issueTracker: IssueTracker
    abstract val trackerRepository: String
}

data class CreateIssueRequest(
    val title: String,
    val body: String,
    val mentionedWireUsers: Collection<UUID>,
    override val issueTracker: IssueTracker,
    override val trackerRepository: String
) : TrackingRequest()

data class CreateConversationForIssueRequest(
    val title: String,
    val issueId: String,
    val wireUsers: Collection<UUID>,
    override val issueTracker: IssueTracker,
    override val trackerRepository: String
) : TrackingRequest() {

    constructor(
        request: CreateIssueRequest,
        issueId: String
    ) : this(
        title = request.title,
        issueId = issueId,
        wireUsers = request.mentionedWireUsers,
        issueTracker = request.issueTracker,
        trackerRepository = request.trackerRepository
    )
}

data class AddCommentRequest(
    val issueId: String,
    val comment: String,
    override val issueTracker: IssueTracker,
    override val trackerRepository: String
) : TrackingRequest()

data class CloseIssueRequest(
    val issueId: String,
    override val issueTracker: IssueTracker,
    override val trackerRepository: String
) : TrackingRequest()

data class CreatedResource(
    val id: String,
    val link: URL
)
