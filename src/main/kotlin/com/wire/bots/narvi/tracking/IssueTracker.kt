package com.wire.bots.narvi.tracking

/**
 * Issue tracker creating and updating issues in the 3rd party APIs.
 */
interface IssueTracker {
    /**
     * Creates issue.
     */
    fun createIssue(request: CreateIssueRequest): CreatedResource

    /**
     * Adds comment to specific issue.
     */
    fun addComment(request: AddCommentRequest): CreatedResource

    /**
     * Closes/Archives the issue.
     */
    fun closeIssue(request: CloseIssueRequest)
}
