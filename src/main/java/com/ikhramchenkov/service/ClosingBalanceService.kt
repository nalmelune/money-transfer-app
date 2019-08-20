package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.ClosingBalanceDao
import com.ikhramchenkov.dto.ClosingBalanceDto
import com.ikhramchenkov.entity.ClosingBalance
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate.now

@Service
class ClosingBalanceService @Inject constructor(
    private val closingBalanceDao: ClosingBalanceDao,
    private val accountsService: AccountsService
) {

    fun getLastClosingBalance(accountNumber: String): ClosingBalanceDto {

        // Verify account exists
        accountsService.findByNumberOrThrow(accountNumber);

        val balance = closingBalanceDao.findByAccountNumberAndLatestDate(accountNumber)

        return ClosingBalanceDto(accountNumber, balance.latestBalanceOrZero(), balance.publishDateOrNow())
    }

    private fun ClosingBalance?.latestBalanceOrZero() = this?.endOfPeriodBalance ?: 0L
    private fun ClosingBalance?.publishDateOrNow() = this?.publishDate ?: now()

}
