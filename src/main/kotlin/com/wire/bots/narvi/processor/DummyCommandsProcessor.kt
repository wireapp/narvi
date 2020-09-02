package com.wire.bots.narvi.processor

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.db.TemplatesService
import com.wire.bots.narvi.db.dto.TemplateDto
import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.server.NarviWireClient
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.CreateTemplateRequest
import com.wire.bots.narvi.tracking.TextSendRequest
import com.wire.bots.narvi.tracking.TrackingRequest
import com.wire.bots.sdk.models.TextMessage
import de.m3y.kformat.Table
import de.m3y.kformat.table
import mu.KLogging
import pw.forst.tools.katlib.newLine
import pw.forst.tools.katlib.whenNull

/**
 * Simple implementation of [CommandsProcessor]
 */
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
            text.startsWith(CREATE_TEMPLATE_TRIGGER) -> createTemplate(message)
            text.startsWith(HELP_TRIGGER) -> sendHelpText()
            text.startsWith(LIST_TEMPLATES_TRIGGER) -> listTemplates()
            else -> commentRequest(message, narviWireClient)
        }.let {
            // maybe some resolver failed and thus it is a comment
            if (it.isNotEmpty()) it
            else commentRequest(message, narviWireClient)
        }
    }

    private fun listTemplates(): Collection<TrackingRequest> {
        val templates = templatesService.getTemplates()
        // render table
        val render = table {
            header("Trigger", "Issue Tracker", "Repository")

            templates.forEach {
                row(it.trigger, it.issueTracker.name, it.repository)
            }

            hints {
                borderStyle = Table.BorderStyle.SINGLE_LINE
            }
        }.render(StringBuilder()).toString().trim()

        return listOf(
            TextSendRequest("```$newLine$render$newLine```")
        )
    }

    private fun createTemplate(message: TextMessage): Collection<TrackingRequest> {
        val (trigger, tracker, repo) = message.text
            .substringAfter(CREATE_TEMPLATE_TRIGGER)
            .split(" ")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .takeIf { it.size == 3 }
            .whenNull { logger.info { "Could not parse template $message" } }
            ?.let { Triple(it[0], it[1], it[2]) }
            ?: return emptyList()

        val issueTracker = IssueTracker.valueOf(tracker.toUpperCase())
        return listOf(
            CreateTemplateRequest(TemplateDto(-1, issueTracker, repo, trigger))
        )
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
            .whenNull { logger.info { "Could not parse $message" } }
            ?: return emptyList()

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

    private fun sendHelpText(): Collection<TrackingRequest> {
        return listOf(
            TextSendRequest(
                """
                    Possible commands:
                    **${CREATE_TEMPLATE_TRIGGER.trim()}** - creates a new template for the issues
                    **${CREATE_ISSUE_TRIGGER.trim()}** - creates a new issue
                    **$CLOSE_ISSUE_TRIGGER** - closes issue associated with the conversation
                    **$LIST_TEMPLATES_TRIGGER** - display all templates in the database
                    **$HELP_TRIGGER** - displays this help
                    
                    Commands parameters syntax:
                    **${CREATE_TEMPLATE_TRIGGER.trim()}** <template name> <tracker> <repository>
                    **${CREATE_ISSUE_TRIGGER.trim()}** <template> <issue name> with <mentions> <new line> <description>

                    Example of **${CREATE_TEMPLATE_TRIGGER.trim()}**:
                    ```
                    ${CREATE_TEMPLATE_TRIGGER.trim()} backend github wireapp/test-repo
                    ```
                    Example of **${CREATE_ISSUE_TRIGGER.trim()}**:
                    ```
                    ${CREATE_ISSUE_TRIGGER.trim()} backend My Awesome Name with @JamesBond @MontyBern
                    Some Description of a very hard problem, we're solving.
                    ```
                """.trimIndent()
            )
        )
    }
}
