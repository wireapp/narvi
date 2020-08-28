package com.wire.bots.narvi.processor

import com.wire.bots.sdk.models.TextMessage
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import pw.forst.tools.katlib.mapToSet
import pw.forst.tools.katlib.newLine
import java.util.UUID
import kotlin.test.assertEquals

internal class CreateMessageParserKtTest {


    @Test
    fun `test parseCreateMessage`() {
        val expectedParsedData = ParsedData(
            issueName = "Some Cool Stuff we did",
            issueDescription = "With all that many things$newLine and this and that",
            templateName = "Backendofc",
            mentionedUsers = (0..5).mapToSet { UUID.randomUUID() }
        )

        val message = textMessage(
            templateName = expectedParsedData.templateName,
            issueName = expectedParsedData.issueName,
            description = expectedParsedData.issueDescription,
            mentions = expectedParsedData.mentionedUsers
        )

        assertEquals(expectedParsedData, parseCreateMessage(message))
    }

    private fun textMessage(
        templateName: String,
        issueName: String,
        description: String,
        mentions: Set<UUID>
    ): TextMessage {
        val headlineTemplate = "$CREATE_ISSUE_TRIGGER $templateName $issueName with "
        val mentionsOptions =
            listOf("@John", "@Jala", "@Trala", "@Boy", "@Johny", "@Stuff")

        val mentionsInString = mentions
            .joinToString(" ") {
                mentionsOptions.random()
            }

        val headline = "$headlineTemplate$mentionsInString"

        val indexedMentions = headline
            .mapIndexedNotNull { index, c -> if (c == '@') index else null }
            .zip(mentions)

        val actualText = "$headline$newLine$description"
        return textMessage(actualText, indexedMentions)
    }

    private fun textMessage(
        messageText: String,
        userMentions: List<Pair<Int, UUID>>
    ): TextMessage {
        val mockMentions =
            userMentions.mapTo(ArrayList()) { (mOffset, mUserId) ->
                TextMessage.Mention().apply {
                    offset = mOffset
                    userId = mUserId
                }
            }

        return mockk {
            every { text } returns messageText
            every { mentions } returns mockMentions
        }
    }
}
