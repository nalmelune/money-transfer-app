package com.ikhramchenkov.dto

import java.util.*

data class TransferRequest(
    val amount: Long,
    val initiatorId: Long
)

data class TransferResponse(
    val operationToken: UUID
)
