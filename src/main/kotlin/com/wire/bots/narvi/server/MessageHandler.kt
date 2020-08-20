package com.wire.bots.narvi.server

import com.wire.bots.sdk.MessageHandlerBase
import com.wire.bots.sdk.WireClient
import com.wire.bots.sdk.models.TextMessage
import mu.KLogging

class MessageHandler : MessageHandlerBase() {

    private companion object : KLogging()

    override fun onText(client: WireClient, msg: TextMessage) {
        super.onText(client, msg)
        logger.info { "New message: ${msg.text}" }
    }
}
