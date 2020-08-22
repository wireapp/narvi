package com.wire.bots.narvi.db

import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.db.model.Issues
import com.wire.bots.narvi.utils.ConversationId
import com.wire.bots.narvi.utils.IssueId
import com.wire.bots.narvi.utils.toUuid
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class IssuesService {

    fun getIssueForConversation(conversationId: ConversationId): Pair<IssueId, IssueTracker>? =
        transaction {
            Issues.slice(Issues.issueId, Issues.issueTracker)
                .select { Issues.conversationId eq conversationId.toString() }
                .firstOrNull()
                ?.let { it[Issues.issueId] to it[Issues.issueTracker] }
        }

    fun getConversationForIssue(issueId: IssueId): ConversationId? =
        transaction {
            Issues.slice(Issues.conversationId)
                .select { Issues.issueId eq issueId }
                .firstOrNull()
                ?.let { it[Issues.conversationId].toUuid() }
        }


    fun insertIssue(
        conversationId: ConversationId,
        issueId: IssueId,
        issueTracker: IssueTracker
    ) {
        transaction {
            Issues.insert {
                it[this.issueId] = issueId
                it[this.conversationId] = conversationId.toString()
                it[this.issueTracker] = issueTracker
            }
        }
    }
}
