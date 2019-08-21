package com.ikhramchenkov.dto

data class AccountInfoDto(
    var accountNumber: String,
    var accountType: String,
    var description: String,
    var balance: Long
) {
    constructor() : this("", "", "", 0)
}