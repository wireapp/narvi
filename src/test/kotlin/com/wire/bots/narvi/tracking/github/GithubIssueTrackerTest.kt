package com.wire.bots.narvi.tracking.github

import com.wire.bots.narvi.db.dto.TemplateDto
import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CloseIssueRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import mu.KLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import pw.forst.tools.katlib.propertiesFromResources
import java.time.Instant
import java.util.UUID

internal class GithubIssueTrackerTest {

    private companion object : KLogging()

    private val props by lazy {
        requireNotNull(propertiesFromResources("/secret.properties"))
    }

    private val token by lazy { props.getProperty("github.testing.token") }
    private val personalRepo by lazy { props.getProperty("github.testing.repo.personal") }
    private val orgRepo by lazy { props.getProperty("github.testing.repo.org") }

    private val github: GitHub
        get() = GitHubBuilder()
            .withOAuthToken(token)
            .build()

    @Test
    @Disabled("integration-test")
    fun `create issue in the private repo`() {
        val result = GithubIssueTracker(github)
            .createIssue(
                CreateIssueRequest(
                    title = "Title - ${UUID.randomUUID()}",
                    body = "Body - some body",
                    mentionedWireUsers = emptySet(),
                    template = TemplateDto(
                        id = 1,
                        IssueTracker.GITHUB,
                        personalRepo
                    )
                )
            )
        logger.info { result }
    }

    @Test
    @Disabled("integration-test")
    fun `create issue in the organization repo`() {
        val result = GithubIssueTracker(github)
            .createIssue(
                CreateIssueRequest(
                    title = "Title - ${UUID.randomUUID()}",
                    body = "Body - some body",
                    mentionedWireUsers = emptySet(),
                    template = TemplateDto(
                        id = 1,
                        IssueTracker.GITHUB,
                        orgRepo
                    )
                )
            )
        logger.info { result }
    }


    @Test
    @Disabled("integration-test")
    fun `add comment to existing issue`() {
        val result = GithubIssueTracker(github)
            .addComment(
                AddCommentRequest(
                    issueId = "1",
                    comment = "Cool comment sent on ${Instant.now()}",
                    template = TemplateDto(
                        id = 1,
                        IssueTracker.GITHUB,
                        personalRepo
                    )
                )
            )
        logger.info { result }
    }

    @Test
    @Disabled("integration-test")
    fun `close existing issue`() {
        GithubIssueTracker(github)
            .closeIssue(
                CloseIssueRequest(
                    issueId = "1",
                    template = TemplateDto(
                        id = 1,
                        IssueTracker.GITHUB,
                        personalRepo
                    )
                )
            )
    }
}

