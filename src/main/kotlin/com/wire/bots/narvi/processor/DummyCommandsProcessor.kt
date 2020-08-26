package com.wire.bots.narvi.processor

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.server.NarviWireClient
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.models.TextMessage
import mu.KLogging
import pw.forst.tools.katlib.mapToSet
import pw.forst.tools.katlib.whenNull
import java.time.Instant
import java.util.UUID

class DummyCommandsProcessor(private val issuesService: IssuesService) : CommandsProcessor {

    private companion object : KLogging() {
        const val CREATE_SEQUENCE_START = "create: "
        const val CLOSE_ISSUE_SEQUENCE = "hey bot close this"
    }

    override fun createTrackingRequestForText(
        message: TextMessage, narviWireClient: NarviWireClient
    ): Collection<TrackingRequest> {
        val text = message.text

        return when {
            text.startsWith(CREATE_SEQUENCE_START) -> createIssue(message)
            text.startsWith(CLOSE_ISSUE_SEQUENCE) -> closeIssue(message)
            else -> commentRequest(message, narviWireClient)
        }
    }

    private fun closeIssue(message: TextMessage): Collection<TrackingRequest> {
        val (issueId, tracker, trackerRepository) = issuesService
            .getIssueForConversation(message.conversationId)
            .whenNull {
                logger.info { "Could not find issue for conversation: ${message.conversationId}, skipping" }
            } ?: return emptyList()

        return listOf(
            CloseIssueRequest(
                trackerRepository = trackerRepository,
                issueId = issueId,
                issueTracker = tracker
            )
        )
    }

    // internal because of testing
    internal fun parseCreateIssueMessage(message: TextMessage): Pair<String, Collection<UUID>> {
        val text = message.text
        val withIdx = text.lastIndexOf("with")
        val users = message.mentions
            .filter { it.offset > withIdx }
            .mapToSet { it.userId!! }
        val issueName = text
            .substring(CREATE_SEQUENCE_START.length - 1, withIdx)
            .trim()
        return issueName to users
    }

    private fun createIssue(message: TextMessage): Collection<TrackingRequest> {
        val (issueName, mentionedUsers) = parseCreateIssueMessage(message)

        return listOf(
            CreateIssueRequest(
                // TODO load that one from configuration
                trackerRepository = "LukasForst/test-repo",
                title = issueName,
                body = "Created by the bot at ${Instant.now()}",
                mentionedWireUsers = mentionedUsers,
                issueTracker = IssueTracker.GITHUB
            )
        )
    }

    private fun commentRequest(
        message: TextMessage, narviWireClient: NarviWireClient
    ): Collection<TrackingRequest> {
        val (issueId, issueTracker, trackerRepository) = issuesService
            .getIssueForConversation(message.conversationId)
            .whenNull {
                logger.info { "Could not find issue for conversation: ${message.conversationId}, skipping" }
            } ?: return emptyList()

        // TODO use names or handles?
        val sayingUser = narviWireClient.getUser(message.userId).name
        val formattedComment = "User $sayingUser says: \"${message.text}\""

        return listOf(
            AddCommentRequest(
                trackerRepository = trackerRepository,
                issueId = issueId,
                comment = formattedComment,
                issueTracker = issueTracker,
            )
        )
    }
}
