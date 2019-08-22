package com.ikhramchenkov

import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.ACCOUNT_NUMBER_1
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.ACCOUNT_NUMBER_2
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.DEFAULT_CLOSING_BALANCE_AMOUNT
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.DEFAULT_TRANSFER_AMOUNT
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.OWNER_ID
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.OWNER_ID_PARAM
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.STRANGER_ACCOUNT_NUMBER
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.STRANGER_ID
import com.ikhramchenkov.api.TransferOwnResource
import com.ikhramchenkov.dao.AccountsDao
import com.ikhramchenkov.dao.BalanceMovementDao
import com.ikhramchenkov.dao.ClosingBalanceDao
import com.ikhramchenkov.dto.TransferRequest
import com.ikhramchenkov.dto.TransferResponse
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.entity.BalanceMovement
import com.ikhramchenkov.entity.ClosingBalance
import com.ikhramchenkov.entity.OperationToken
import com.ikhramchenkov.exception.AttemptToUseStrangersAccountException
import com.ikhramchenkov.exception.InsufficientFundsException
import com.ikhramchenkov.exception.SameAccountsInRequestException
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceMovementService
import com.ikhramchenkov.service.BalanceService
import com.ikhramchenkov.service.ClosingBalanceService
import io.dropwizard.jersey.errors.ErrorMessage
import io.dropwizard.testing.junit.DAOTestRule
import io.dropwizard.testing.junit.ResourceTestRule
import org.apache.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE

class TransferOwnResourceIntegrationTest {

    val DEFAULT_ACCOUNT_1 = AccountEntity(
        accountNumber = ACCOUNT_NUMBER_1, ownerId = OWNER_ID,
        accountType = "SavingsAccount", description = "My first savings account"
    )
    val DEFAULT_ACCOUNT_2 = AccountEntity(
        accountNumber = ACCOUNT_NUMBER_2, ownerId = OWNER_ID,
        accountType = "SavingsAccount", description = "Family savings account"
    )
    val STRANGER_ACCOUNT = AccountEntity(
        accountNumber = STRANGER_ACCOUNT_NUMBER, ownerId = STRANGER_ID,
        accountType = "SavingsAccount", description = "Family savings account"
    )
    val DEFAULT_CLOSING_BALANCE = ClosingBalance(
        accountNumber = ACCOUNT_NUMBER_1,
        publishDate = LocalDate.now().minusDays(1),
        endOfPeriodBalance = DEFAULT_CLOSING_BALANCE_AMOUNT,
        summaryPeriodCredit = 642300,
        summaryPeriodDebit = 345604
    )

    @Test
    fun shouldTransferBetweenOwnAccounts() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)

        closingBalanceDao.save(DEFAULT_CLOSING_BALANCE)
        balanceMovementService.saveMovementsBetweenAccounts(
            ACCOUNT_NUMBER_1,
            ACCOUNT_NUMBER_2,
            DEFAULT_TRANSFER_AMOUNT
        )

        val operationAmount = 8650L

        val result = resources.target("$TRANSFER_PATH/$ACCOUNT_NUMBER_1/$ACCOUNT_NUMBER_2")
            .queryParam(OWNER_ID_PARAM, OWNER_ID)
            .request().post(Entity.entity(TransferRequest(operationAmount, OWNER_ID), APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_CREATED, result.status)

        assertNotNull(result.readEntity(TransferResponse::class.java).operationToken)

        assertEquals(
            DEFAULT_CLOSING_BALANCE_AMOUNT - DEFAULT_TRANSFER_AMOUNT - operationAmount,
            balanceService.getAccountBalance(ACCOUNT_NUMBER_1)
        )
    }

    @Test
    fun shouldThrowIfSameAccounts() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)

        val operationAmount = 8650L

        val result = resources.target("$TRANSFER_PATH/$ACCOUNT_NUMBER_1/$ACCOUNT_NUMBER_1")
            .request().post(Entity.entity(TransferRequest(operationAmount, OWNER_ID), APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.status)
        assertEquals(SameAccountsInRequestException.REASON, result.readEntity(ErrorMessage::class.java).message)
    }

    @Test
    fun shouldThrowIfStrangersAccount1() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)

        val operationAmount = 8650L

        val result = resources.target("$TRANSFER_PATH/$ACCOUNT_NUMBER_1/$ACCOUNT_NUMBER_2")
            .request().post(Entity.entity(TransferRequest(operationAmount, STRANGER_ID), APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_FORBIDDEN, result.status)
        assertEquals(AttemptToUseStrangersAccountException.REASON, result.readEntity(ErrorMessage::class.java).message)
    }

    @Test
    fun shouldThrowIfStrangersAccount2() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(STRANGER_ACCOUNT)

        val operationAmount = 8650L

        val result = resources.target("$TRANSFER_PATH/$ACCOUNT_NUMBER_1/$STRANGER_ACCOUNT_NUMBER")
            .request().post(Entity.entity(TransferRequest(operationAmount, OWNER_ID), APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_FORBIDDEN, result.status)
        assertEquals(AttemptToUseStrangersAccountException.REASON, result.readEntity(ErrorMessage::class.java).message)
    }

    @Test
    fun shouldThrowIfInsufficientFunds() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(DEFAULT_ACCOUNT_2)

        val operationAmount = 8650L

        val result = resources.target("$TRANSFER_PATH/$ACCOUNT_NUMBER_1/$ACCOUNT_NUMBER_2")
            .request().post(Entity.entity(TransferRequest(operationAmount, OWNER_ID), APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_FORBIDDEN, result.status)
        assertEquals(InsufficientFundsException.REASON, result.readEntity(ErrorMessage::class.java).message)
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
        .addResource(TransferOwnResource(accountsService, balanceService, balanceMovementService))
        .build()!!

    companion object {
        const val TRANSFER_PATH = "/transfer"
    }
}