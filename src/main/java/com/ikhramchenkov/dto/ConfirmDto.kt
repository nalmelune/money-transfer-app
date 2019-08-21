package com.ikhramchenkov.dto

import java.util.*
import javax.validation.constraints.NotNull

data class ConfirmRequestDto(
    @NotNull val ownerId: Long
)

data class ConfirmResponseDto(
    val operationUUID: UUID
)