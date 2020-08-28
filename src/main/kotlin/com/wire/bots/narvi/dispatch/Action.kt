package com.wire.bots.narvi.dispatch

import com.wire.bots.narvi.server.NarviWireClient
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateConversationForIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.CreateTemplateRequest

sealed class Action {
    abstract val client: NarviWireClient
}

data class SendTextAction(
    val message: String,
    override val client: NarviWireClient
) : Action()

data class CreateIssueAction(
    val request: CreateIssueRequest,
    override val client: NarviWireClient
) : Action()

data class CloseIssueAction(
    val request: CloseIssueRequest,
    override val client: NarviWireClient
) : Action()

data class AddCommentAction(
    val request: AddCommentRequest,
    override val client: NarviWireClient
) : Action()

data class CreateConversationForIssueAction(
    val request: CreateConversationForIssueRequest,
    override val client: NarviWireClient
) : Action()

data class CreateTemplateAction(
    val request: CreateTemplateRequest,
    override val client: NarviWireClient
) : Action()
