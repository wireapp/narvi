package com.wire.bots.narvi.tracking.github

import com.wire.bots.narvi.db.IssuesService
import com.wire.bots.narvi.dispatch.ActionDispatcher
import com.wire.bots.narvi.dispatch.SendTextAction
import com.wire.bots.narvi.server.NarviClientRepo
import com.wire.bots.narvi.server.resources.GitWebhook
import com.wire.bots.narvi.server.resources.Issue
import com.wire.bots.narvi.server.resources.User
import mu.KLogging
import org.kohsuke.github.GitHub
import pw.forst.tools.katlib.whenNull
import pw.forst.tools.katlib.whenTrue

/**
 * Handler for incoming webhooks, uses dispatcher to dispatch actions.
 */
class GithubWebhookHandler(
    private val repo: NarviClientRepo,
    private val dispatcher: ActionDispatcher,
    private val github: GitHub,
    private val issuesService: IssuesService
) {

    private companion object : KLogging()

    private val usedLogin by lazy { github.myself.login }

    /**
     * Handles incoming webhook.
     */
    fun handle(type: String, webhook: GitWebhook) {
        wasProducedByMyself(webhook.sender).whenTrue { return } // guard infinite loop
        when (type) {
            "issues" -> handleIssue(webhook)
            "issue_comment" -> handleIssueComment(webhook)
            else -> logger.info { "Unhandled type: $type for hook $webhook" }
        }
    }

    private fun handleIssue(webhook: GitWebhook) {
        val issue = requireNotNull(webhook.issue) { "Issue action with no issue!" }

        wasProducedByMyself(issue.user).whenTrue { return } // guard infinite loop

        when (webhook.action) {
            "closed" -> {
                val wireClient = clientFor(issue) ?: return

                dispatcher.dispatch(
                    SendTextAction(
                        "*Issue #${issue.number} was closed on the remote tracker.*",
                        wireClient
                    )
                )
            }
        }
    }


    private fun handleIssueComment(webhook: GitWebhook) {
        val comment = requireNotNull(webhook.comment) { "Comment event with comment set to null! $webhook" }
        val issue = requireNotNull(webhook.issue) { "Issue comment without issue! $webhook" }

        wasProducedByMyself(comment.user).whenTrue { return } // guard infinite loop

        val wireClient = clientFor(issue) ?: return

        dispatcher.dispatch(
            SendTextAction(
                """
                    **@${comment.user.login}** wrote:
                    ${comment.body}
                """.trimIndent(),
                wireClient
            )
        )
    }

    private fun wasProducedByMyself(user: User?) =
        (user?.login == usedLogin).whenTrue { logger.info { "Ignoring, event from myself." } }

    private fun clientFor(issue: Issue) =
        issuesService.getConversationIdForIssue(issue.number.toString())
            ?.let { repo.getClient(it) }
            .whenNull { logger.info { "Conversation not found for issue #${issue.number}, skipping" } }
}
