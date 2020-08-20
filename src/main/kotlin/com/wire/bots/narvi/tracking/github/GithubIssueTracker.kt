package com.wire.bots.narvi.tracking.github

import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.CreatedResource
import com.wire.bots.narvi.tracking.IssueTracker
import org.kohsuke.github.GitHub


class GithubIssueTracker(private val github: GitHub) : IssueTracker {

    override fun createIssue(request: CreateIssueRequest) =
        github.getRepository(request.repository)
            .createIssue(request.title)
            .body(request.body)
            .create()
            .let {
                CreatedResource(
                    id = it.number.toString(),
                    link = it.htmlUrl
                )
            }

    override fun addComment(request: AddCommentRequest) =
        github.getRepository(request.repository)
            .getIssue(request.issueId.toInt())
            .comment(request.comment)
            .let {
                CreatedResource(
                    id = it.id.toString(),
                    link = it.htmlUrl
                )
            }

    override fun closeIssue(request: CloseIssueRequest) =
        github.getRepository(request.repository)
            .getIssue(request.issueId.toInt())
            .close()
}
