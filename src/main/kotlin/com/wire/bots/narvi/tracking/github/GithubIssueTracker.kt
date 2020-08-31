package com.wire.bots.narvi.tracking.github

import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import com.wire.bots.narvi.tracking.CreatedResource
import com.wire.bots.narvi.tracking.IssueTracker
import com.wire.bots.narvi.tracking.TemplateTrackingRequests
import org.kohsuke.github.GitHub
import com.wire.bots.narvi.db.model.IssueTracker as EnumTracker

/**
 * Implementation of [IssueTracker] for Github
 */
class GithubIssueTracker(private val github: GitHub) : IssueTracker {

    override fun createIssue(request: CreateIssueRequest) =
        guardGithub(request) {
            getRepository(request.template.repository)
                .createIssue(request.title)
                .body(request.body)
                .create()
                .let {
                    CreatedResource(
                        id = it.number.toString(),
                        link = it.htmlUrl
                    )
                }
        }

    override fun addComment(request: AddCommentRequest) =
        guardGithub(request) {
            getRepository(request.template.repository)
                .getIssue(request.issueId.toInt())
                .comment(request.comment)
                .let {
                    CreatedResource(
                        id = it.id.toString(),
                        link = it.htmlUrl
                    )
                }
        }

    override fun closeIssue(request: CloseIssueRequest) =
        guardGithub(request) {
            getRepository(request.template.repository)
                .getIssue(request.issueId.toInt())
                .close()
        }

    private inline fun <T> guardGithub(request: TemplateTrackingRequests, block: (GitHub.() -> (T))): T {
        require(request.template.issueTracker == EnumTracker.GITHUB) {
            "Incompatible tracker request! $request"
        }
        return block(github)
    }
}
