package com.wire.bots.narvi.db.model

import org.jetbrains.exposed.sql.Table

object Issues : Table() {
    val issueId = varchar("issue_id", 256)
    val issueTracker = enumerationByName("issue_tracker", 256, IssueTracker::class)

    // uuid
    val conversationId = varchar("conversation_id", 36)
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(issueId, issueTracker, conversationId)
}
