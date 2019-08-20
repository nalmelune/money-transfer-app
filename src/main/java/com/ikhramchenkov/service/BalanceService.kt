package com.ikhramchenkov.service

import com.google.inject.Inject
import com.google.inject.Singleton
import com.ikhramchenkov.exception.InsufficientFundsException

@Singleton
class BalanceService @Inject constructor(
    private val balanceMovementService: BalanceMovementService,
    private val closingBalanceService: ClosingBalanceService
) {
    /**
     * Let's assume balance can fit into Long at any economy situation having 2 "cents" digits.
     *
     * @return result would either be actual balance or zero. if account doesn't exist there should be no values,
     * so there's no reason to check if account exists
     */
    fun getAccountBalance(accountNumber: String): Long {
        val lastPeriodBalance = closingBalanceService.getLastClosingBalance(accountNumber)

        val debitSum = balanceMovementService.getDebitSince(accountNumber, lastPeriodBalance.closingDate)
        val creditSum = balanceMovementService.getCreditSince(accountNumber, lastPeriodBalance.closingDate)

        return lastPeriodBalance.balance + debitSum - creditSum;
    }

    fun checkEnoughBalanceOrThrow(accountNumber: String, requestedAmount: Long) {
        val balance: Long = getAccountBalance(accountNumber)
        if (balance < requestedAmount) {
            throw InsufficientFundsException()
        }
    }
}