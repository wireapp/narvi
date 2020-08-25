package com.wire.bots.narvi.server

import com.waz.model.Messages
import com.wire.bots.narvi.dispatch.ActionDispatcher
import com.wire.bots.narvi.dispatch.AddCommentAction
import com.wire.bots.narvi.dispatch.CloseIssueAction
import com.wire.bots.narvi.dispatch.CreateIssueAction
import com.wire.bots.narvi.processor.CommandsProcessor
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.MessageHandlerBase
import com.wire.bots.sdk.WireClient
import com.wire.bots.sdk.models.TextMessage
import mu.KLogging
import java.util.UUID

class MessageHandler(
    private val actionDispatcher: ActionDispatcher,
    private val commandsProcessor: CommandsProcessor
) : MessageHandlerBase() {

    private companion object : KLogging()

    override fun onEvent(client: WireClient, userId: UUID, genericMessage: Messages.GenericMessage) {
        logger.info { "New event: $genericMessage" }
    }

    override fun onText(client: WireClient, msg: TextMessage) {
        logger.info { "New message: ${msg.text}" }
        val actions = commandsProcessor
            .createTrackingRequestForText(msg)
            .map { it.convert(client) }
        actionDispatcher.dispatch(actions)
    }

    private fun TrackingRequest.convert(client: WireClient) =
        when (this) {
            is CreateIssueRequest -> CreateIssueAction(this, client)
            is AddCommentRequest -> AddCommentAction(this, client)
            is CloseIssueRequest -> CloseIssueAction(this, client)
        }
}
