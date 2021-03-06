package com.wire.bots.narvi.server

import com.wire.bots.narvi.dispatch.ActionDispatcher
import com.wire.bots.narvi.dispatch.AddCommentAction
import com.wire.bots.narvi.dispatch.CloseIssueAction
import com.wire.bots.narvi.dispatch.CreateConversationForIssueAction
import com.wire.bots.narvi.dispatch.CreateIssueAction
import com.wire.bots.narvi.dispatch.CreateTemplateAction
import com.wire.bots.narvi.dispatch.SendTextAction
import com.wire.bots.narvi.processor.CommandsProcessor
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateConversationForIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.CreateTemplateRequest
import com.wire.bots.narvi.tracking.TextSendRequest
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.MessageHandlerBase
import com.wire.bots.sdk.WireClient
import com.wire.bots.sdk.models.TextMessage
import com.wire.bots.sdk.user.UserClient
import mu.KLogging

class MessageHandler(
    private val actionDispatcher: ActionDispatcher,
    private val commandsProcessor: CommandsProcessor
) : MessageHandlerBase() {

    private companion object : KLogging()

    override fun onText(client: WireClient, msg: TextMessage) {
        runCatching {
            require(client is UserClient) { "Bot requires user mode enabled!" }
            val narviClient = NarviWireClient(client)

            logger.info { "New message: ${msg.text}" }
            val actions = commandsProcessor
                .createTrackingRequestForText(msg, narviClient)
                .map { it.convert(narviClient) }

            actionDispatcher.dispatch(actions)
        }.onFailure {
            logger.error(it) { "Exception during onText handling!" }
        }
    }

    private fun TrackingRequest.convert(narviClient: NarviWireClient) =
        when (this) {
            is CreateIssueRequest -> CreateIssueAction(this, narviClient)
            is AddCommentRequest -> AddCommentAction(this, narviClient)
            is CloseIssueRequest -> CloseIssueAction(this, narviClient)
            is CreateConversationForIssueRequest -> CreateConversationForIssueAction(this, narviClient)
            is CreateTemplateRequest -> CreateTemplateAction(this, narviClient)
            is TextSendRequest -> SendTextAction(message, narviClient)
        }
}
