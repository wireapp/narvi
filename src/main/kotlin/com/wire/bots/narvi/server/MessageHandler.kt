package com.wire.bots.narvi.server

import com.waz.model.Messages
import com.wire.bots.sdk.MessageHandlerBase
import com.wire.bots.sdk.WireClient
import com.wire.bots.sdk.models.TextMessage
import com.wire.bots.sdk.server.model.NewBot
import mu.KLogging
import java.util.UUID

class MessageHandler : MessageHandlerBase() {

    private companion object : KLogging()


    override fun onEvent(client: WireClient, userId: UUID, genericMessage: Messages.GenericMessage) {
        super.onEvent(client, userId, genericMessage)
        logger.info { "New event: $genericMessage" }
    }

    override fun onNewBot(newBot: NewBot, serviceToken: String): Boolean {
        logger.info { "New bot ${newBot.client}" }
        return true
    }

    override fun onText(client: WireClient, msg: TextMessage) {
        super.onText(client, msg)
        logger.info { "New message: ${msg.text}" }
    }
}
