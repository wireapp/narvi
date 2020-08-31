package com.wire.bots.narvi.tracking

import com.wire.bots.narvi.db.dto.TemplateDto
import java.net.URL
import java.util.UUID

interface TemplateTrackingRequests {
    val template: TemplateDto
}

sealed class TrackingRequest

data class TextSendRequest(
    val message: String
) : TrackingRequest()

data class CreateTemplateRequest(
    override val template: TemplateDto
) : TemplateTrackingRequests, TrackingRequest()

data class CreateIssueRequest(
    val title: String,
    val body: String,
    val mentionedWireUsers: Set<UUID>,
    override val template: TemplateDto
) : TemplateTrackingRequests, TrackingRequest()

data class CreateConversationForIssueRequest(
    val title: String,
    val issueId: String,
    val wireUsers: Set<UUID>,
    override val template: TemplateDto
) : TemplateTrackingRequests, TrackingRequest() {

    constructor(
        request: CreateIssueRequest,
        issueId: String
    ) : this(
        title = request.title,
        issueId = issueId,
        wireUsers = request.mentionedWireUsers,
        template = request.template
    )
}

data class AddCommentRequest(
    val issueId: String,
    val comment: String,
    override val template: TemplateDto
) : TemplateTrackingRequests, TrackingRequest()

data class CloseIssueRequest(
    val issueId: String,
    override val template: TemplateDto
) : TemplateTrackingRequests, TrackingRequest()

data class CreatedResource(
    val id: String,
    val link: URL
)
