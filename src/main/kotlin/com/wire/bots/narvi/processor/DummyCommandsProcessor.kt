package com.wire.bots.narvi.processor

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.db.TemplatesService
import com.wire.bots.narvi.server.NarviWireClient
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.models.TextMessage
import mu.KLogging
import pw.forst.tools.katlib.whenNull

class DummyCommandsProcessor(
    private val issuesService: IssuesService,
    private val templatesService: TemplatesService
) : CommandsProcessor {

    private companion object : KLogging();

    override fun createTrackingRequestForText(
        message: TextMessage, narviWireClient: NarviWireClient
    ): Collection<TrackingRequest> {
        val text = message.text

        return when {
            text.startsWith(CREATE_ISSUE_TRIGGER) -> createIssue(message)
            text.startsWith(CLOSE_ISSUE_TRIGGER) -> closeIssue(message)
            else -> commentRequest(message, narviWireClient)
        }
    }

    private fun closeIssue(message: TextMessage): Collection<TrackingRequest> {
        val issue = issuesService
            .getIssueForConversation(message.conversationId)
            .whenNull {
                logger.info { "Could not find issue for conversation: ${message.conversationId}, skipping" }
            } ?: return emptyList()

        return listOf(
            CloseIssueRequest(
                issueId = issue.issueId,
                template = issue.template
            )
        )
    }

    private fun createIssue(message: TextMessage): Collection<TrackingRequest> {
        val parsedData = parseCreateMessage(message)
        val template = templatesService
            .templateForTrigger(parsedData.templateName)
            .whenNull { logger.warn { "Unknown template! $message" } }
            ?: return emptyList()

        return listOf(
            CreateIssueRequest(
                title = parsedData.issueName,
                body = parsedData.issueDescription,
                mentionedWireUsers = parsedData.mentionedUsers + message.userId,
                template = template
            )
        )
    }

    private fun commentRequest(
        message: TextMessage, narviWireClient: NarviWireClient
    ): Collection<TrackingRequest> {
        val issue = issuesService
            .getIssueForConversation(message.conversationId)
            .whenNull {
                logger.info { "Could not find issue for conversation: ${message.conversationId}, skipping" }
            } ?: return emptyList()

        // TODO use names or handles?
        val sayingUser = narviWireClient.getUser(message.userId).name
        val formattedComment = "**$sayingUser** says:\n> ${message.text}"

        return listOf(
            AddCommentRequest(
                issueId = issue.issueId,
                comment = formattedComment,
                template = issue.template
            )
        )
    }
}
