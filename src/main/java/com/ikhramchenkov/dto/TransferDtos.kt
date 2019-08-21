package com.ikhramchenkov.dto

import java.util.*
import javax.validation.constraints.NotNull

data class TransferRequest(
    @NotNull val amount: Long?,
    @NotNull val initiatorId: Long?
) {
    constructor() : this(null, null)
}

data class TransferResponse(
    val operationToken: UUID?
) {
    constructor() : this(null)
}
