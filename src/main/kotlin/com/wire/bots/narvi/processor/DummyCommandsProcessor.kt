package com.wire.bots.narvi.processor

import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.models.TextMessage
import mu.KLogging
import java.time.Instant

class DummyCommandsProcessor : CommandsProcessor {

    private companion object : KLogging()

    override fun createTrackingRequestForText(message: TextMessage): Collection<TrackingRequest> =
        when {
            message.text.startsWith("create") -> listOf(
                CreateIssueRequest(
                    repository = "LukasForst/test-repo",
                    title = message.text.substringAfter("create "),
                    body = "Created by the bot at ${Instant.now()}",
                    issueTracker = IssueTracker.GITHUB
                )
            )
            message.text.startsWith("close") -> listOf(
                CloseIssueRequest(
                    repository = "LukasForst/test-repo",
                    issueId = message.text.substringAfter("close "),
                    issueTracker = IssueTracker.GITHUB
                )
            )
            else -> listOf(
                AddCommentRequest(
                    repository = "LukasForst/test-repo",
                    issueId = message.text.split(" ").first(),
                    comment = "User ${message.userId} says: \"${message.text.split(" ").drop(1).joinToString(" ")}\"",
                    issueTracker = IssueTracker.GITHUB
                )
            )
        }
}
