package com.wire.bots.narvi.dispatch

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.tracking.CreateConversationForIssueRequest
import com.wire.bots.narvi.tracking.IssueTracker
import mu.KLogging

class SynchronousActionDispatcher(
    private val issueTracker: IssueTracker,
    private val issuesService: IssuesService
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
                        CreateConversationForIssueRequest(action.request, it.id),
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
                issueTracker = action.request.issueTracker,
                trackerRepository = action.request.trackerRepository
            )
        }.onFailure {
            logger.error(it) { "It was not possible to create conversation for request: ${action.request}" }
        }.onSuccess {
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
            logger.info { "Issue ${action.request.issueId} closed." }
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
            // todo send or not to send comment links?
//            dispatch(SendTextAction("Comment added: ${it.link}", action.client))
        }
    }
}
