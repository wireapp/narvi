package com.wire.bots.narvi.db.model

import org.jetbrains.exposed.sql.Table

object Templates : Table() {
    val id = integer("id")

    val issueTracker = enumerationByName("issue_tracker", 256, IssueTracker::class)
    val trackerRepository = varchar("tracker_repository", 256)
    val trigger = varchar("trigger", 256).uniqueIndex()
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}
