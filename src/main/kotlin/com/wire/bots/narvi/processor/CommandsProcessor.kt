package com.wire.bots.narvi.processor

import com.wire.bots.narvi.server.NarviWireClient
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.models.TextMessage

/**
 * Creating request DTOs from the given messages.
 */
interface CommandsProcessor {
    /**
     * Parses message and creates request DTO.
     */
    fun createTrackingRequestForText(message: TextMessage, narviWireClient: NarviWireClient): Collection<TrackingRequest>
}
