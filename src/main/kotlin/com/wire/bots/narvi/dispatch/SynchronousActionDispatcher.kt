package com.wire.bots.narvi.dispatch

import com.wire.bots.narvi.tracking.IssueTracker
import mu.KLogging

class SynchronousActionDispatcher(
    private val issueTracker: IssueTracker
) : ActionDispatcher {

    private companion object : KLogging()

    override fun dispatch(action: Action): Unit =
        when (action) {
            is SendTextAction -> sendTextAction(action)
            is CreateIssueAction -> createIssueAction(action)
            is CloseIssueAction -> closeIssueActon(action)
            is AddCommentAction -> addCommentAction(action)
        }

    private fun sendTextAction(action: SendTextAction) {
        logger.info { "Sending message." }
        runCatching {
            action.client.sendText(action.message)
        }.onFailure {
            logger.error(it) { "Exception during sending text ${action.message}" }
        }.onSuccess {
            logger.info { "Text sent with id $it" }
        }
    }

    private fun createIssueAction(action: CreateIssueAction) {
        logger.info { "Creating new issue: ${action.request}" }
        runCatching {
            issueTracker.createIssue(action.request)
        }.onFailure {
            logger.error(it) { "It was not possible to create issue: ${action.request}" }
        }.onSuccess {
            logger.info { "Issue created under ID ${it.id}" }
            dispatch(SendTextAction("Issue created - ${it.link}", action.client))
        }
    }

    private fun closeIssueActon(action: CloseIssueAction) {
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

    private fun addCommentAction(action: AddCommentAction) {
        logger.info { "Adding comment ${action.request}" }
        runCatching {
            issueTracker.addComment(action.request)
        }.onFailure {
            logger.error(it) { "It was not possible to add comment: ${action.request}" }
        }.onSuccess {
            logger.info { "Comment created under id: ${it.id}" }
            dispatch(SendTextAction("Comment added: ${it.link}", action.client))
        }
    }
}
