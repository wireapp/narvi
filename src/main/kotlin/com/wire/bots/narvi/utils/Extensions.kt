package com.wire.bots.narvi.utils

import java.util.UUID

fun String.toUuid(): UUID = UUID.fromString(this)
