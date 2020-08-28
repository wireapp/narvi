package com.wire.bots.narvi.db

import com.wire.bots.narvi.db.dto.TemplateDto
import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.db.model.Templates
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class TemplatesService(private val db: Lazy<Database>) {

    fun templateForTrigger(trigger: String) =
        transaction(db.value) {
            Templates
                .select { Templates.trigger eq trigger }
                .singleOrNull()
                ?.let {
                    TemplateDto(
                        id = it[Templates.id],
                        issueTracker = it[Templates.issueTracker],
                        repository = it[Templates.trackerRepository]
                    )
                }
        }

    fun insertTemplate(
        issueTracker: IssueTracker,
        repository: String,
        trigger: String
    ) =
        transaction(db.value) {
            Templates.insert {
                it[this.issueTracker] = issueTracker
                it[this.trackerRepository] = repository
                it[this.trigger] = trigger
            }
        }
}
