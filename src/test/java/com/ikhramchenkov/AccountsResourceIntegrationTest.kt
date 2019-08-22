package com.ikhramchenkov

import com.ikhramchenkov.api.AccountsResource
import com.ikhramchenkov.dao.AccountsDao
import com.ikhramchenkov.dao.BalanceMovementDao
import com.ikhramchenkov.dao.ClosingBalanceDao
import com.ikhramchenkov.dto.AccountInfoDto
import com.ikhramchenkov.dto.AccountInfoListDto
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.entity.BalanceMovement
import com.ikhramchenkov.entity.ClosingBalance
import com.ikhramchenkov.entity.OperationToken
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceMovementService
import com.ikhramchenkov.service.BalanceService
import com.ikhramchenkov.service.ClosingBalanceService
import io.dropwizard.testing.junit.DAOTestRule
import io.dropwizard.testing.junit.ResourceTestRule
import org.apache.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import javax.ws.rs.core.Response

class AccountsResourceIntegrationTest {

    val DEFAULT_ACCOUNT_1 = AccountEntity(
        accountNumber = ACCOUNT_NUMBER_1, ownerId = OWNER_ID,
        accountType = "SavingsAccount", description = "My first savings account"
    )
    val DEFAULT_ACCOUNT_2 = AccountEntity(
        accountNumber = ACCOUNT_NUMBER_2, ownerId = OWNER_ID,
        accountType = "SavingsAccount", description = "Family savings account"
    )
    val DEFAULT_CLOSING_BALANCE = ClosingBalance(
        accountNumber = ACCOUNT_NUMBER_1, publishDate = LocalDate.now().minusDays(1),
        endOfPeriodBalance = DEFAULT_CLOSING_BALANCE_AMOUNT, summaryPeriodCredit = 642300, summaryPeriodDebit = 345604
    )

    @Test
    fun shouldThrowOnEmptyGet() {
        val result = resources.target(ACCOUNTS_PATH)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.status)
    }

    /**
     * We have no way to prove in this implementation if there's an owner that has no accounts,
     * Or there's just no owner, so let's pretend there're not accounts to return
     */
    @Test
    fun shouldReturnEmptyOnNoAccounts() {
        val result = resources.target(ACCOUNTS_PATH)
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_OK, result.status)
        assertEquals(AccountInfoListDto(), result.readEntity(AccountInfoListDto::class.java))
    }

    @Test
    fun shouldReturnTwoAccountsWithZeroBalance() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)

        val result = resources.target(ACCOUNTS_PATH)
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_OK, result.status)

        val accountInfoListDto = result.readEntity(AccountInfoListDto::class.java)
        val accountInfoDto1 = accountInfoListDto.accountInfoDtos[0]
        val accountInfoDto2 = accountInfoListDto.accountInfoDtos[1]

        assertEquals(ACCOUNT_NUMBER_1, accountInfoDto1.accountNumber)
        assertEquals(ACCOUNT_NUMBER_2, accountInfoDto2.accountNumber)
        assertEquals(0L, accountInfoDto1.balance)
        assertEquals(0L, accountInfoDto2.balance)
    }

    @Test
    fun shouldReturnTwoAccountsWithOneNotZeroBalance() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)
        closingBalanceDao.save(DEFAULT_CLOSING_BALANCE)
        balanceMovementService.saveMovementsBetweenAccounts(
            ACCOUNT_NUMBER_1,
            STRANGER_ACCOUNT_NUMBER,
            DEFAULT_TRANSFER_AMOUNT
        )

        val result = resources.target(ACCOUNTS_PATH)
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_OK, result.status)

        val accountInfoListDto = result.readEntity(AccountInfoListDto::class.java)
        val accountInfoDto1 = accountInfoListDto.accountInfoDtos[0]
        val accountInfoDto2 = accountInfoListDto.accountInfoDtos[1]

        assertEquals(ACCOUNT_NUMBER_1, accountInfoDto1.accountNumber)
        assertEquals(ACCOUNT_NUMBER_2, accountInfoDto2.accountNumber)
        assertEquals(DEFAULT_CLOSING_BALANCE_AMOUNT - DEFAULT_TRANSFER_AMOUNT, accountInfoDto1.balance)
        assertEquals(0L, accountInfoDto2.balance)
    }

    @Test
    fun shouldReturnSingleAccountInformation() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)
        closingBalanceDao.save(DEFAULT_CLOSING_BALANCE)
        balanceMovementService.saveMovementsBetweenAccounts(
            ACCOUNT_NUMBER_1,
            STRANGER_ACCOUNT_NUMBER,
            DEFAULT_TRANSFER_AMOUNT
        )

        val result = resources.target("$ACCOUNTS_PATH/$ACCOUNT_NUMBER_1")
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_OK, result.status)

        val accountInfoDto = result.readEntity(AccountInfoDto::class.java)

        assertEquals(ACCOUNT_NUMBER_1, accountInfoDto.accountNumber)
        assertEquals(DEFAULT_CLOSING_BALANCE_AMOUNT - DEFAULT_TRANSFER_AMOUNT, accountInfoDto.balance)
    }

    @Test
    fun shouldReturnEmptyAccountInformationBecauseOtherAccountOperationsDontAffectThisOne() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)
        closingBalanceDao.save(DEFAULT_CLOSING_BALANCE)
        balanceMovementService.saveMovementsBetweenAccounts(
            ACCOUNT_NUMBER_1,
            STRANGER_ACCOUNT_NUMBER,
            DEFAULT_TRANSFER_AMOUNT
        )

        val result = resources.target("$ACCOUNTS_PATH/$ACCOUNT_NUMBER_2")
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_OK, result.status)

        val accountInfoDto = result.readEntity(AccountInfoDto::class.java)

        assertEquals(ACCOUNT_NUMBER_2, accountInfoDto.accountNumber)
        assertEquals(0L, accountInfoDto.balance)
    }

    @Test
    fun shouldThrowIfStrangerAccountDetail() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)

        val result = resources.target("$ACCOUNTS_PATH/$ACCOUNT_NUMBER_1")
            .queryParam(OWNER_ID_PARAM, STRANGER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_FORBIDDEN, result.status)
    }

    @Test
    fun shouldThrowIfThereIsNoAccount() {
        val result = resources.target("$ACCOUNTS_PATH/$ACCOUNT_NUMBER_1")
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().get(Response::class.java)

        assertEquals(HttpStatus.SC_NOT_FOUND, result.status)
    }

    @Rule
    @JvmField
    val database = DAOTestRule.newBuilder()
        .addEntityClass(AccountEntity::class.java)
        .addEntityClass(BalanceMovement::class.java)
        .addEntityClass(ClosingBalance::class.java)
        .addEntityClass(OperationToken::class.java)
        .build()!!

    private val accountsDao = AccountsDao(database.sessionFactory)
    private val balanceMovementDao = BalanceMovementDao(database.sessionFactory)
    private val closingBalanceDao = ClosingBalanceDao(database.sessionFactory)

    private val accountsService = AccountsService(accountsDao)
    private val balanceMovementService = BalanceMovementService(balanceMovementDao)
    private val closingBalanceService = ClosingBalanceService(closingBalanceDao, accountsService)
    private val balanceService = BalanceService(balanceMovementService, closingBalanceService)

    @Rule
    @JvmField
    val resources = ResourceTestRule.builder()
        .addResource(AccountsResource(accountsService, balanceService))
        .build()!!

    companion object {
        const val ACCOUNTS_PATH = "/accounts"
        const val OWNER_ID_PARAM = "ownerId"
        const val OWNER_ID = 42L
        const val STRANGER_ID = 666L
        const val DEFAULT_CLOSING_BALANCE_AMOUNT = 43204L
        const val DEFAULT_TRANSFER_AMOUNT = 3204L
        const val ACCOUNT_NUMBER_1 = "408178103000000000001"
        const val ACCOUNT_NUMBER_2 = "408178103000000000002"
        const val STRANGER_ACCOUNT_NUMBER = "408178103000000000003"
    }
}