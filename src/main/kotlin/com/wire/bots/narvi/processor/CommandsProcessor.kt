package com.wire.bots.narvi.processor

import com.wire.bots.narvi.server.NarviWireClient
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.models.TextMessage

interface CommandsProcessor {
    fun createTrackingRequestForText(message: TextMessage, narviWireClient: NarviWireClient): Collection<TrackingRequest>
}
