package com.wire.bots.narvi.tracking

interface IssueTracker {
    fun createIssue(request: CreateIssueRequest): CreatedResource

    fun addComment(request: AddCommentRequest): CreatedResource

    fun closeIssue(request: CloseIssueRequest)
}
