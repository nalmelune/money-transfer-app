package com.ikhramchenkov.dto

import java.util.*

data class ConfirmRequestDto(
    val ownerId: Long
)

data class ConfirmResponseDto(
    val operationUUID: UUID
)