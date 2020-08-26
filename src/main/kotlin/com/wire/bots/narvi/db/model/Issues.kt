package com.wire.bots.narvi.db.model

import org.jetbrains.exposed.sql.Table

object Issues : Table() {
    val narviId = integer("narvi_id")
    val issueId = varchar("issue_id", 256)
    val issueTracker = enumerationByName("issue_tracker", 256, IssueTracker::class)
    val trackerRepository = varchar("tracker_repository", 256)

    // uuid
    val conversationId = varchar("conversation_id", 36)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(narviId)
}
