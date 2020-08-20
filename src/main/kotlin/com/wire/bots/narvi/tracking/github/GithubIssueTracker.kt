package com.wire.bots.narvi.tracking.github

import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.CreatedResource
import com.wire.bots.narvi.tracking.IssueTracker
import org.kohsuke.github.GitHub


class GithubIssueTracker(private val github: GitHub) : IssueTracker {

    override fun createIssue(request: CreateIssueRequest): CreatedResource {
        val issue = github
            .getRepository(request.repository)
            .createIssue(request.title)
            .body(request.body)
            .create()

        return CreatedResource(
            id = issue.number.toString(),
            link = issue.htmlUrl
        )
    }

    override fun addComment(request: AddCommentRequest): CreatedResource {
        val comment = github.getRepository(request.repository)
            .getIssue(request.issueId.toInt())
            .comment(request.comment)
        return CreatedResource(
            id = comment.id.toString(),
            link = comment.htmlUrl
        )
    }
}
