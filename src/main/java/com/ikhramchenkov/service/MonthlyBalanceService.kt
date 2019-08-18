package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.MonthlyBalanceDao
import com.ikhramchenkov.dto.ClosingBalanceDto
import com.ikhramchenkov.utils.lastDateOfPreviousMonth
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate
import java.time.LocalDate.now

@Service
class MonthlyBalanceService {

    @Inject
    private lateinit var monthlyBalanceDao: MonthlyBalanceDao

    @Inject
    private lateinit var accountsService: AccountsService

    fun getLastMonthBalance(accountNumber: String): ClosingBalanceDto {

        // Verify account exists
        accountsService.findByNumberOrThrow(accountNumber);

        val dateToLookPreviousMonthFrom = now()
        var dateFrom = lastDateOfPreviousMonth(dateToLookPreviousMonthFrom);

        var balance = monthlyBalanceDao.findByAccountNumberAndDate(accountNumber, dateFrom)

        if (balance == null) {
            dateFrom = lastDateOfPreviousMonth(nowMinusMonth(dateToLookPreviousMonthFrom))

            balance = monthlyBalanceDao.findByAccountNumberAndDate(accountNumber, dateFrom)
        }

        return ClosingBalanceDto(accountNumber, balance?.endOfTheMonthBalance ?: 0L, dateFrom)
    }

    private fun nowMinusMonth(now: LocalDate): LocalDate = now.minusMonths(1)
}
