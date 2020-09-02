package com.wire.bots.narvi.db

import com.wire.bots.narvi.db.dto.IssueDto
import com.wire.bots.narvi.db.dto.TemplateDto
import com.wire.bots.narvi.db.model.Issues
import com.wire.bots.narvi.db.model.Templates
import com.wire.bots.narvi.utils.ConversationId
import com.wire.bots.narvi.utils.IssueId
import com.wire.bots.narvi.utils.toUuid
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class IssuesService(private val db: Database) {

    fun getIssueForConversation(conversationId: ConversationId) =
        transaction(db) {
            (Issues innerJoin Templates)
                .select { Issues.conversationId eq conversationId.toString() }
                .firstOrNull()
                ?.let {
                    IssueDto(
                        issueId = it[Issues.issueId],
                        conversationId = it[Issues.conversationId].toUuid(),
                        template = TemplateDto(
                            id = it[Templates.id],
                            issueTracker = it[Templates.issueTracker],
                            repository = it[Templates.trackerRepository],
                            trigger = it[Templates.trigger]
                        )
                    )
                }
        }

    fun getConversationIdForIssue(issueId: IssueId) =
        transaction(db) {
            (Issues innerJoin Templates)
                .select { Issues.issueId eq issueId }
                .firstOrNull()
                ?.let {
                    it[Issues.conversationId].toUuid()
                }
        }


    fun insertIssue(
        conversationId: ConversationId,
        issueId: IssueId,
        templateId: Int
    ) {
        transaction(db) {
            Issues.insert {
                it[this.issueId] = issueId
                it[this.conversationId] = conversationId.toString()
                it[this.templateId] = templateId
            }
        }
    }
}
