package com.ikhramchenkov.dto

import java.util.*

data class ReserveRequestDto(
    val accountNumberFrom: String,
    val ownerId: Long,
    val accountNumberTo: String,
    val amount: Long
)

data class ReserveResponseDto(
    val token: UUID,
    // there's suppose to be more user data about to who it's going to be sent, but let's simplify
    val ownerId: Long
)