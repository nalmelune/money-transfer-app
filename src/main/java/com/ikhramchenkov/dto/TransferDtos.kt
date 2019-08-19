package com.ikhramchenkov.dto

import java.util.*

class TransferRequest(val amount: Long, val initiatorId: Long)

class TransferResponse(val operationNumber: UUID)
