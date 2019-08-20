package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.OperationTokenDao
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.entity.OperationToken
import org.jvnet.hk2.annotations.Service
import java.util.*

@Service
class OperationTokenService {

    @Inject
    private lateinit var operationTokenDao: OperationTokenDao

    fun saveNewToken(accountFrom: AccountEntity, accountTo: AccountEntity, requestedAmount: Long): UUID =
        UUID.randomUUID().also {
            operationTokenDao.save(
                OperationToken(it, accountFrom.accountNumber!!, accountTo.accountNumber!!, requestedAmount, accountFrom.ownerId)
            )
        }

    fun findByToken(token: UUID) =
        operationTokenDao.findByToken(token)
}
