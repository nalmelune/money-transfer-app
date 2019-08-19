package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.AccountsDao
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.exception.AccountNotFoundException
import org.jvnet.hk2.annotations.Service

@Service
class AccountsService {

    @Inject
    private lateinit var accountsDao: AccountsDao

    fun findByOwner(ownerId: Long): List<AccountEntity> = accountsDao.findByOwner(ownerId)

    fun findByNumberOrThrow(accountNumber: String) =
        accountsDao.findByNumber(accountNumber) ?: throw AccountNotFoundException(accountNumber);

    fun lock(accountEntity: AccountEntity) {
        accountsDao.lock(accountEntity)
    }

}