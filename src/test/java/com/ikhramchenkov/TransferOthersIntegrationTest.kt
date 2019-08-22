package com.ikhramchenkov

import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.ACCOUNT_NUMBER_1
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.ACCOUNT_NUMBER_2
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.DEFAULT_CLOSING_BALANCE_AMOUNT
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.DEFAULT_TRANSFER_AMOUNT
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.OWNER_ID
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.STRANGER_ACCOUNT_NUMBER
import com.ikhramchenkov.AccountsResourceIntegrationTest.Companion.STRANGER_ID
import com.ikhramchenkov.api.TransferOthersResource
import com.ikhramchenkov.dao.AccountsDao
import com.ikhramchenkov.dao.BalanceMovementDao
import com.ikhramchenkov.dao.ClosingBalanceDao
import com.ikhramchenkov.dao.OperationTokenDao
import com.ikhramchenkov.dto.ConfirmRequestDto
import com.ikhramchenkov.dto.ConfirmResponseDto
import com.ikhramchenkov.dto.ReserveRequestDto
import com.ikhramchenkov.dto.ReserveResponseDto
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.entity.BalanceMovement
import com.ikhramchenkov.entity.ClosingBalance
import com.ikhramchenkov.entity.OperationToken
import com.ikhramchenkov.exception.AttemptToUseStrangersAccountException
import com.ikhramchenkov.exception.NoSuchTokenException
import com.ikhramchenkov.service.*
import io.dropwizard.jersey.errors.ErrorMessage
import io.dropwizard.testing.junit.DAOTestRule
import io.dropwizard.testing.junit.ResourceTestRule
import org.apache.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.*
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

class TransferOthersIntegrationTest {

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
        accountNumber = ACCOUNT_NUMBER_1, publishDate = LocalDate.now().minusDays(1),
        endOfPeriodBalance = DEFAULT_CLOSING_BALANCE_AMOUNT, summaryPeriodCredit = 642300, summaryPeriodDebit = 345604
    )

    @Test
    fun shouldReserveTokenAndTransfer() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(STRANGER_ACCOUNT)

        closingBalanceDao.save(DEFAULT_CLOSING_BALANCE)
        balanceMovementService.saveMovementsBetweenAccounts(ACCOUNT_NUMBER_1, ACCOUNT_NUMBER_2, DEFAULT_TRANSFER_AMOUNT)

        val operationAmount = 8650L

        val resultOfReserve = resources.target(RESERVE_PATH).request()
            .post(
                Entity.entity(
                    ReserveRequestDto(ACCOUNT_NUMBER_1, OWNER_ID, STRANGER_ACCOUNT_NUMBER, operationAmount),
                    MediaType.APPLICATION_JSON_TYPE
                )
            )

        assertEquals(HttpStatus.SC_CREATED, resultOfReserve.status)

        val token = resultOfReserve.readEntity(ReserveResponseDto::class.java).run {
            assertEquals(STRANGER_ID, ownerId)
            assertNotNull(token)
            token
        }

        val resultOfConfirm = resources.target("$CONFIRM_PATH/$token").request()
            .post(Entity.entity(ConfirmRequestDto(OWNER_ID), MediaType.APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_OK, resultOfConfirm.status)

        val confirmResponse = resultOfConfirm.readEntity(ConfirmResponseDto::class.java)

        assertNotNull(confirmResponse.operationUUID)
    }

    @Test
    fun shouldThrowIfNoToken() {
        val result = resources.target("$CONFIRM_PATH/${UUID.randomUUID()}").request()
            .post(Entity.entity(ConfirmRequestDto(OWNER_ID), MediaType.APPLICATION_JSON_TYPE))

        assertEquals(HttpStatus.SC_NOT_FOUND, result.status)
        assertEquals(NoSuchTokenException.REASON, result.readEntity(ErrorMessage::class.java).message)
    }

    @Test
    fun shouldThrowIfStrangersAccount() {
        accountsDao.save(DEFAULT_ACCOUNT_1)
        accountsDao.save(STRANGER_ACCOUNT)

        val operationAmount = 8650L

        val result = resources.target(RESERVE_PATH).request()
            .post(
                Entity.entity(
                    ReserveRequestDto(ACCOUNT_NUMBER_1, STRANGER_ID, STRANGER_ACCOUNT_NUMBER, operationAmount),
                    MediaType.APPLICATION_JSON_TYPE
                )
            )

        assertEquals(HttpStatus.SC_FORBIDDEN, result.status)

        assertEquals(AttemptToUseStrangersAccountException.REASON, result.readEntity(ErrorMessage::class.java).message)
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
    private val operationTokenDao = OperationTokenDao(database.sessionFactory)
    private val accountsService = AccountsService(accountsDao)
    private val balanceMovementService = BalanceMovementService(balanceMovementDao)
    private val closingBalanceService = ClosingBalanceService(closingBalanceDao, accountsService)
    private val balanceService = BalanceService(balanceMovementService, closingBalanceService)
    private val operationTokenService = OperationTokenService(operationTokenDao)

    @Rule
    @JvmField
    val resources = ResourceTestRule.builder()
        .addResource(
            TransferOthersResource(accountsService, balanceService, balanceMovementService, operationTokenService)
        ).build()!!

    companion object {
        const val RESERVE_PATH = "/reserve"
        const val CONFIRM_PATH = "/confirm"
    }

}