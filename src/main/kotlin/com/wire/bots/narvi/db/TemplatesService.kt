package com.wire.bots.narvi.db

import com.wire.bots.narvi.db.dto.TemplateDto
import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.db.model.Templates
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TemplatesService(private val db: Database) {

    fun templateForTrigger(trigger: String) =
        transaction(db) {
            Templates
                .select { Templates.trigger eq trigger }
                .singleOrNull()
                ?.toTemplateDto()
        }

    fun getTemplates() =
        transaction(db) {
            Templates.selectAll().map { it.toTemplateDto() }
        }

    fun insertTemplate(
        issueTracker: IssueTracker,
        repository: String,
        trigger: String
    ): Unit =
        transaction(db) {
            Templates.insert {
                it[this.issueTracker] = issueTracker
                it[this.trackerRepository] = repository
                it[this.trigger] = trigger
            }
        }

    private fun ResultRow.toTemplateDto() =
        TemplateDto(
            id = this[Templates.id],
            issueTracker = this[Templates.issueTracker],
            repository = this[Templates.trackerRepository],
            trigger = this[Templates.trigger]
        )

}
