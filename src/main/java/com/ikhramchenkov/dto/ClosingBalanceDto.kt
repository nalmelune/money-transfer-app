package com.ikhramchenkov.dto

import java.time.LocalDate

data class ClosingBalanceDto(
    val accountNumber: String,
    val balance: Long,
    val closingDate: LocalDate
)