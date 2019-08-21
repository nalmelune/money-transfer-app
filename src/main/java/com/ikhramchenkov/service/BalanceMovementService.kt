package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.BalanceMovementDao
import com.ikhramchenkov.entity.BalanceMovement
import com.ikhramchenkov.enumeration.TransactionType
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import com.ikhramchenkov.enumeration.TransactionType.D as DEPOSIT
import com.ikhramchenkov.enumeration.TransactionType.W as WITHDRAWAL

@Service
class BalanceMovementService @Inject constructor(private val balanceMovementDao: BalanceMovementDao) {

    fun getDebitSince(accountNumber: String, dateSince: LocalDate): Long =
        balanceMovementDao.getDebitSince(accountNumber, dateSince)

    fun getCreditSince(accountNumber: String, dateSince: LocalDate): Long =
        balanceMovementDao.getCreditSince(accountNumber, dateSince)

    fun saveMovementsBetweenAccounts(
        from: String,
        to: String,
        amount: Long
    ): UUID {
        return UUID.randomUUID().also { token ->
            val now = LocalDateTime.now()
            balanceMovementDao.save(toBalanceMovement(from, amount, WITHDRAWAL, token, now))
            balanceMovementDao.save(toBalanceMovement(to, amount, DEPOSIT, token, now))
        }
    }

    private fun toBalanceMovement(
        accountNumber: String,
        amount: Long,
        type: TransactionType,
        operationNumber: UUID,
        createdAt: LocalDateTime
    ) = BalanceMovement(
        accountNumber = accountNumber,
        amount = amount,
        transactionType = type,
        operationNumber = operationNumber,
        createdAt = createdAt
    )
}
