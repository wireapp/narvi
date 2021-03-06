package com.wire.bots.narvi.processor

import com.wire.bots.sdk.models.TextMessage
import pw.forst.tools.katlib.mapToSet
import pw.forst.tools.katlib.newLine
import java.util.UUID

/**
 * Parses issues metadata.
 */
fun parseCreateMessage(message: TextMessage): ParsedData? {
    // first line is headline, everything else is issue description
    val (headline, body) = message.text.split(newLine)
        .let { it.first().trim() to it.drop(1).joinToString(newLine) }

    // first word after create is name of template
    val templateName = headline
        .substringAfter(CREATE_ISSUE_TRIGGER)
        .trim()
        .split(" ")
        .firstOrNull()
        ?: return null

    // find idx after which everything is users
    val withIdx = headline.lastIndexOf("with")
    val mentionedUsers =
        if (withIdx > 0) {
            message.mentions
                .filter { it.offset > withIdx && it.offset < headline.length }
                .mapToSet { it.userId!! }
        } else emptySet()

    val nameEndIndex = if (withIdx > 0) withIdx else headline.length

    // parse issue name
    val issueName = headline
        .substring(0 until nameEndIndex)
        .substringAfter("$CREATE_ISSUE_TRIGGER$templateName")
        .trim()

    return ParsedData(
        issueName = issueName,
        issueDescription = body,
        templateName = templateName,
        mentionedUsers = mentionedUsers
    )
}

data class ParsedData(
    val issueName: String,
    val issueDescription: String,
    val templateName: String,
    val mentionedUsers: Set<UUID>
)
