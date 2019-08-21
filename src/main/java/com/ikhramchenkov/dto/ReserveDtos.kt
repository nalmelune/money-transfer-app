package com.ikhramchenkov.dto

import java.util.*
import javax.validation.constraints.NotNull

data class ReserveRequestDto(
    @NotNull val accountNumberFrom: String,
    @NotNull val ownerId: Long,
    @NotNull val accountNumberTo: String,
    @NotNull val amount: Long
)

data class ReserveResponseDto(
    val token: UUID,
    // there's suppose to be more user data about to who it's going to be sent, but let's simplify
    val ownerId: Long
)