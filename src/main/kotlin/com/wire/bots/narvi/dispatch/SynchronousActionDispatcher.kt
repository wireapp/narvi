package com.wire.bots.narvi.dispatch

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.db.TemplatesService
import com.wire.bots.narvi.server.NarviClientRepo
import com.wire.bots.narvi.tracking.CreateConversationForIssueRequest
import com.wire.bots.narvi.tracking.IssueTracker
import mu.KLogging

/**
 * Synchronously dispatch actions one by one.
 */
class SynchronousActionDispatcher(
    private val issueTracker: IssueTracker,
    private val issuesService: IssuesService,
    private val templateService: TemplatesService,
    private val clientRepo: NarviClientRepo
) : ActionDispatcher {

    private companion object : KLogging()

    override fun dispatch(actions: Collection<Action>) =
        actions.forEach { dispatch(it) }

    override fun dispatch(action: Action): Unit =
        when (action) {
            is SendTextAction -> dispatch(action)
            is CreateIssueAction -> dispatch(action)
            is CloseIssueAction -> dispatch(action)
            is AddCommentAction -> dispatch(action)
            is CreateConversationForIssueAction -> dispatch(action)
            is CreateTemplateAction -> dispatch(action)
        }

    private fun dispatch(action: CreateTemplateAction) {
        logger.info { "Creating new template" }
        val template = action.request.template
        runCatching {
            templateService.insertTemplate(
                issueTracker = template.issueTracker,
                repository = template.repository,
                trigger = template.trigger
            )
        }.onFailure {
            logger.error(it) { "It was not possible to create template ${template.trigger}" }
        }.onSuccess {
            logger.info { "Template \"${template.trigger}\" created" }
            dispatch(SendTextAction("Template \"${template.trigger}\" created", action.client))
        }
    }

    private fun dispatch(action: SendTextAction) {
        logger.info { "Sending message." }
        runCatching {
            action.client.sendText(action.message)
        }.onFailure {
            logger.error(it) { "Exception during sending text ${action.message}" }
        }.onSuccess {
            logger.info { "Text sent with id $it" }
        }
    }

    private fun dispatch(action: CreateIssueAction) {
        logger.info { "Creating new issue: ${action.request}" }
        runCatching {
            issueTracker.createIssue(action.request)
        }.onFailure {
            logger.error(it) { "It was not possible to create issue: ${action.request}" }
        }.onSuccess {
            logger.info { "Issue created under ID ${it.id}" }
            dispatch(
                listOf(
                    CreateConversationForIssueAction(
                        CreateConversationForIssueRequest(action.request, it.id, it.link),
                        action.client
                    ),
                    SendTextAction("Issue created - ${it.link}", action.client)
                )
            )
        }
    }

    private fun dispatch(action: CreateConversationForIssueAction) {
        logger.info { "Creating conversation for issue: ${action.request}" }
        runCatching {
            val conversation = action.client.createConversation(
                "[#${action.request.issueId}] - ${action.request.title}",
                action.request.wireUsers.toList()
            )

            issuesService.insertIssue(
                conversationId = conversation.id,
                issueId = action.request.issueId,
                templateId = action.request.template.id
            )
            conversation
        }.onFailure {
            logger.error(it) { "It was not possible to create conversation for request: ${action.request}" }
        }.onSuccess {
            dispatch(
                SendTextAction(
                    """
                        Conversation for issue [#${action.request.issueId} ${action.request.title}](${action.request.issueLink})
                        *Please note that all messages sent to this conversation will be recorded in the issue tracking system.*
                    """.trimIndent(),
                    clientRepo.getClient(it.id)
                )
            )
            logger.info { "Conversation created for issue id ${action.request.issueId}" }
        }
    }

    private fun dispatch(action: CloseIssueAction) {
        logger.info { "Closing issue ${action.request}" }
        runCatching {
            issueTracker.closeIssue(action.request)
        }.onFailure {
            logger.error(it) { "It was not possible to close issue: ${action.request}" }
        }.onSuccess {
            logger.info { "Issue #${action.request.issueId} closed." }
            dispatch(SendTextAction("Issue ${action.request.issueId} closed.", action.client))
        }
    }

    private fun dispatch(action: AddCommentAction) {
        logger.info { "Adding comment ${action.request}" }
        runCatching {
            issueTracker.addComment(action.request)
        }.onFailure {
            logger.error(it) { "It was not possible to add comment: ${action.request}" }
        }.onSuccess {
            logger.info { "Comment created under id: ${it.id}" }
        }
    }
}
