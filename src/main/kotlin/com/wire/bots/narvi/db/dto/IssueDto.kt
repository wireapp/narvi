package com.wire.bots.narvi.db.dto

import com.wire.bots.narvi.utils.ConversationId
import com.wire.bots.narvi.utils.IssueId

data class IssueDto(
    val issueId: IssueId,
    val conversationId: ConversationId,
    val template: TemplateDto
)
