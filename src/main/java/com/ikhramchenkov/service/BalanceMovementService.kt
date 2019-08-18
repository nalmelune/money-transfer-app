package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.BalanceMovementDao
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate

@Service
class BalanceMovementService {

    @Inject
    private lateinit var balanceMovementDao: BalanceMovementDao

    fun getDebitSince(accountNumber: String, dateSince: LocalDate): Long =
        balanceMovementDao.getDebitSince(accountNumber, dateSince)

    fun getCreditSince(accountNumber: String, dateSince: LocalDate): Long =
        balanceMovementDao.getCreditSince(accountNumber, dateSince)


}
