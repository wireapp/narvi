package com.wire.bots.narvi.tracking.github

import com.wire.bots.narvi.tracking.AddCommentRequest
import com.wire.bots.narvi.tracking.CreateIssueRequest
import mu.KLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.kohsuke.github.GitHubBuilder
import java.time.Instant
import java.util.Properties
import java.util.UUID

internal class GithubIssueTrackerTest {

    private companion object : KLogging()

    private val props by lazy {
        Properties().apply {
            load(javaClass.getResourceAsStream("/secret.properties"))
        }
    }

    private val token by lazy { props.getProperty("github.testing.token") }
    private val personalRepo by lazy { props.getProperty("github.testing.repo.personal") }
    private val orgRepo by lazy { props.getProperty("github.testing.repo.org") }

    @Test
    @Disabled("integration-test")
    fun `create issue in the private repo`() {
        val github = GitHubBuilder()
            .withOAuthToken(token)
            .build()

        val result = GithubIssueTracker(github)
            .createIssue(
                CreateIssueRequest(
                    personalRepo,
                    "Title - ${UUID.randomUUID()}",
                    "Body - some body"
                )
            )
        logger.info { result }
    }

    @Test
    @Disabled("integration-test")
    fun `create issue in the organization repo`() {
        val github = GitHubBuilder()
            .withOAuthToken(token)
            .build()

        val result = GithubIssueTracker(github)
            .createIssue(
                CreateIssueRequest(
                    orgRepo,
                    "Test",
                    "Test"
                )
            )
        logger.info { result }
    }


    @Test
    @Disabled("integration-test")
    fun `add comment to existing issue`() {
        val github = GitHubBuilder()
            .withOAuthToken(token)
            .build()

        val result = GithubIssueTracker(github)
            .addComment(
                AddCommentRequest(
                    personalRepo,
                    "1",
                    "Cool comment sent on ${Instant.now()}"
                )
            )
        logger.info { result }
    }
}

