package com.ikhramchenkov.utils

import com.ikhramchenkov.entity.AccountEntity
import java.util.*

fun sortedMapOfAccounts(
    accountFrom: AccountEntity,
    accountTo: AccountEntity
): SortedMap<String, AccountEntity> = sortedMapOf<String, AccountEntity>().apply {
    this[accountFrom.accountNumber!!] = accountFrom
    this[accountTo.accountNumber!!] = accountTo
}