package com.wire.bots.narvi.tracking

import com.wire.bots.narvi.tracking.github.GithubIssueTracker
import com.wire.bots.narvi.db.model.IssueTracker as EnumTracker

/**
 * Implementation of issue tracker which can distinguish between different trackers
 * and send request to the correct one.
 */
class AggregatingIssueTracker(
    private val githubIssueTracker: GithubIssueTracker
) : IssueTracker {

    override fun createIssue(request: CreateIssueRequest) =
        tracker(request.template.issueTracker).createIssue(request)

    override fun addComment(request: AddCommentRequest) =
        tracker(request.template.issueTracker).addComment(request)

    override fun closeIssue(request: CloseIssueRequest) =
        tracker(request.template.issueTracker).closeIssue(request)

    private fun tracker(tracker: EnumTracker): IssueTracker =
        when (tracker) {
            EnumTracker.GITHUB -> githubIssueTracker
        }
}
