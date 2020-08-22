package com.wire.bots.narvi.db.model

import org.jetbrains.exposed.sql.Table

object Usernames : Table() {
    val wireUsername = varchar("wire_user_name", 256)
    val issueTracker = enumerationByName("issue_tracker", 256, IssueTracker::class)
    val trackerUsername = varchar("tracker_username", 256)
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(wireUsername, issueTracker)
}
