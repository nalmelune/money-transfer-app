package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.AccountsDao
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.exception.AccountNotFoundException
import com.ikhramchenkov.utils.sortedMapOfAccounts
import org.jvnet.hk2.annotations.Service

@Service
class AccountsService @Inject constructor(private val accountsDao: AccountsDao) {

    fun findByOwner(ownerId: Long): List<AccountEntity> = accountsDao.findByOwner(ownerId)

    fun findByNumberOrThrow(accountNumber: String): AccountEntity =
        accountsDao.findByNumber(accountNumber)
            ?: throw AccountNotFoundException(accountNumber);

    fun lock(accountFrom: AccountEntity, accountTo: AccountEntity) {
        sortedMapOfAccounts(accountFrom, accountTo).forEach {
            accountsDao.lock(it.value)
        }
    }

}