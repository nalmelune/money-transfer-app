package com.ikhramchenkov.dto

data class AccountInfoListDto(var accountInfoDtos: List<AccountInfoDto>) {
    constructor() : this(emptyList())
}