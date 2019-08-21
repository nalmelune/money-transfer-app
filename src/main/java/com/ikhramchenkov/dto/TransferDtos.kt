package com.ikhramchenkov.dto

import java.util.*
import javax.validation.constraints.NotNull

data class TransferRequest(
    @NotNull val amount: Long,
    @NotNull val initiatorId: Long
)

data class TransferResponse(
    val operationToken: UUID
)
