package com.wire.bots.narvi.db

import com.wire.bots.narvi.db.dto.TemplateDto
import com.wire.bots.narvi.db.model.Templates
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class TemplatesService {

    fun templateForTrigger(trigger: String) =
        transaction {
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
}
