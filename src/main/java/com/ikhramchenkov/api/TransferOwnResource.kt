package com.ikhramchenkov.api

import com.google.inject.Inject
import com.ikhramchenkov.dto.TransferRequest
import com.ikhramchenkov.dto.TransferResponse
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.entity.BalanceMovement
import com.ikhramchenkov.enumeration.TransactionType
import com.ikhramchenkov.exception.AttemptToUseStrangersAccountException
import com.ikhramchenkov.exception.InsufficientFundsException
import com.ikhramchenkov.exception.SameAccountsInRequestException
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceMovementService
import com.ikhramchenkov.service.BalanceService
import io.dropwizard.hibernate.UnitOfWork
import java.util.*
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED
import com.ikhramchenkov.enumeration.TransactionType.D as DEPOSIT
import com.ikhramchenkov.enumeration.TransactionType.W as WITHDRAWAL

@Produces(APPLICATION_JSON)
class TransferOwnResource {

    @Inject
    private lateinit var accountsService: AccountsService

    @Inject
    private lateinit var balanceService: BalanceService

    @Inject
    private lateinit var balanceMovementService: BalanceMovementService

    @POST
    @Path("/transfer/{from}/{to}")
    @UnitOfWork
    fun transferBetweenOwnAccounts(
        @PathParam("from") from: String,
        @PathParam("to") to: String,
        transferRequest: TransferRequest
    ): Response {
        if (from == to) throw SameAccountsInRequestException()
        // 1.1 check "from" exists
        val accountFrom = accountsService.findByNumberOrThrow(from)

        // 2 check "to" exists
        val accountTo = accountsService.findByNumberOrThrow(to)

        // 3 check owner is initiator
        // Let's assume here should be implementation for identification of owner
        if (accountFrom.ownerId != transferRequest.initiatorId) throw AttemptToUseStrangersAccountException()

        // 4 lock "from" and "to" on db ordering locks by number
        sortedMapOfAccounts(accountFrom, accountTo).forEach {
            accountsService.lock(it.value)
        }

        // 5 check "from" has enough balance
        val balance: Long = balanceService.getAccountBalance(accountFrom.accountNumber!!)
        if (balance < transferRequest.amount) {
            throw InsufficientFundsException()
        }

        // 6 "append" the movements to db
        val operationNumber = UUID.randomUUID()
        balanceMovementService.save(toBalanceMovement(to, transferRequest.amount, WITHDRAWAL, operationNumber))
        balanceMovementService.save(toBalanceMovement(to, transferRequest.amount, DEPOSIT, operationNumber))

        // 7 send event of changing balance (would I really need to?) async way (isn't it async already?)

        // 8 return response with operation number (should there be an endpoint url? hateoas)
        return Response.status(CREATED).entity(TransferResponse(operationNumber)).build()
    }

    private fun sortedMapOfAccounts(
        accountFrom: AccountEntity,
        accountTo: AccountEntity
    ): SortedMap<String, AccountEntity> = sortedMapOf<String, AccountEntity>().apply {
        this[accountFrom.accountNumber!!] = accountFrom
        this[accountTo.accountNumber!!] = accountTo
    }

    private fun toBalanceMovement(
        accountNumber: String,
        amount: Long,
        type: TransactionType,
        operationNumber: UUID
    ) = BalanceMovement(
        accountNumber = accountNumber, amount = amount, transactionType = type, operationNumber = operationNumber
    )
}
