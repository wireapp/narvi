package com.wire.bots.narvi.dispatch

import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.sdk.WireClient

sealed class Action {
    abstract val client: WireClient
}

data class SendTextAction(
    val message: String,
    override val client: WireClient
) : Action()

data class CreateIssueAction(
    val request: CreateIssueRequest,
    override val client: WireClient
) : Action()

data class CloseIssueAction(
    val request: CloseIssueRequest,
    override val client: WireClient
) : Action()

data class AddCommentAction(
    val request: AddCommentRequest,
    override val client: WireClient
) : Action()
