package com.wire.bots.narvi.db.dto

import com.wire.bots.narvi.db.model.IssueTracker

data class TemplateDto(
    val id: Int,
    val issueTracker: IssueTracker,
    val repository: String,
    val trigger: String
)
