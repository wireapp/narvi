package com.wire.bots.narvi.db.model

import org.jetbrains.exposed.sql.Table

object Issues : Table("issues") {
    val narviId = integer("narvi_id")
    val issueId = varchar("issue_id", 256)
    val templateId = integer("template_id") references Templates.id

    // uuid
    val conversationId = varchar("conversation_id", 36)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(narviId)
}
