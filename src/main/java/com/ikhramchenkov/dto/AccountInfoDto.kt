package com.ikhramchenkov.dto

data class AccountInfoDto(
    val description: String,
    val accountType: String,
    val accountName: String,
    val balance: Long
)