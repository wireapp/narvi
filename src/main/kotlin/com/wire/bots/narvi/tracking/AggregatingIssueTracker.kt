package com.wire.bots.narvi.tracking

import com.wire.bots.narvi.tracking.github.GithubIssueTracker
import com.wire.bots.narvi.db.model.IssueTracker as EnumTracker

class AggregatingIssueTracker(
    private val githubIssueTracker: GithubIssueTracker
) : IssueTracker {

    override fun createIssue(request: CreateIssueRequest) =
        tracker(request.issueTracker).createIssue(request)

    override fun addComment(request: AddCommentRequest) =
        tracker(request.issueTracker).addComment(request)

    override fun closeIssue(request: CloseIssueRequest) =
        tracker(request.issueTracker).closeIssue(request)

    private fun tracker(tracker: EnumTracker): IssueTracker =
        when (tracker) {
            EnumTracker.GITHUB -> githubIssueTracker
        }
}
