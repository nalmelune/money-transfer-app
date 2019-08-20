package com.ikhramchenkov.api

import com.google.inject.Inject
import com.ikhramchenkov.dto.ConfirmRequestDto
import com.ikhramchenkov.dto.ConfirmResponseDto
import com.ikhramchenkov.dto.ReserveRequestDto
import com.ikhramchenkov.dto.ReserveResponseDto
import com.ikhramchenkov.exception.AttemptToUseStrangersAccountException
import com.ikhramchenkov.exception.NoSuchTokenException
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceMovementService
import com.ikhramchenkov.service.BalanceService
import com.ikhramchenkov.service.OperationTokenService
import io.dropwizard.hibernate.UnitOfWork
import java.util.*
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED

@Produces(APPLICATION_JSON)
class TransferOthersResource {

    @Inject
    private lateinit var accountsService: AccountsService

    @Inject
    private lateinit var balanceService: BalanceService

    @Inject
    private lateinit var balanceMovementService: BalanceMovementService

    @Inject
    private lateinit var operationTokenService: OperationTokenService

    @POST
    @Path("/reserve")
    @UnitOfWork
    fun reserveAttemptToSend(request: ReserveRequestDto): Response {

        val accountFrom = accountsService.findByNumberOrThrow(request.accountNumberFrom)

        val accountTo = accountsService.findByNumberOrThrow(request.accountNumberTo)

        // Let's assume here should be implementation for identification of owner
        if (accountFrom.ownerId != request.ownerId) throw AttemptToUseStrangersAccountException()

        accountsService.lock(accountFrom, accountTo)

        balanceService.checkEnoughBalanceOrThrow(accountFrom.accountNumber!!, request.amount)

        val operationToken = operationTokenService.saveNewToken(accountFrom, accountTo, request.amount)

        return Response.status(CREATED).entity(ReserveResponseDto(operationToken, accountFrom.ownerId!!)).build()
    }

    @POST
    @Path("/confirm/{token}")
    @UnitOfWork
    fun confirmSending(
        @PathParam("token") token: String,
        request: ConfirmRequestDto
    ): Response {

        val operationToken =
            operationTokenService.findByToken(UUID.fromString(token)) ?: throw NoSuchTokenException()

        val accountFrom = accountsService.findByNumberOrThrow(operationToken.accountFrom!!)

        val accountTo = accountsService.findByNumberOrThrow(operationToken.accountTo!!)

        // Let's assume here should be implementation for identification of owner
        if (operationToken.ownerId != request.ownerId) throw AttemptToUseStrangersAccountException()

        accountsService.lock(accountFrom, accountTo)

        balanceService.checkEnoughBalanceOrThrow(operationToken.accountFrom, operationToken.amount!!)

        val operationUUID = balanceMovementService.saveMovementsBetweenAccounts(
            operationToken.accountFrom,
            operationToken.accountTo,
            operationToken.amount
        )

        return Response.status(CREATED).entity(ConfirmResponseDto(operationUUID)).build()
    }
}
