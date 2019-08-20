package com.ikhramchenkov.api

import com.google.inject.Inject
import com.ikhramchenkov.dto.TransferRequest
import com.ikhramchenkov.dto.TransferResponse
import com.ikhramchenkov.exception.AttemptToUseStrangersAccountException
import com.ikhramchenkov.exception.SameAccountsInRequestException
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceMovementService
import com.ikhramchenkov.service.BalanceService
import io.dropwizard.hibernate.UnitOfWork
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED

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
        if (accountTo.ownerId != transferRequest.initiatorId) throw AttemptToUseStrangersAccountException()

        // 4 lock "from" and "to" on db ordering locks by number
        accountsService.lock(accountFrom, accountTo)

        // 5 check "from" has enough balance
        balanceService.checkEnoughBalanceOrThrow(accountFrom.accountNumber!!, transferRequest.amount)

        // 6 "append" the movements to db
        val operationToken = balanceMovementService.saveMovementsBetweenAccounts(from, to, transferRequest.amount)

        // 7 send event of changing balance (would I really need to?) async way (isn't it async already?)

        // 8 return response with operation number (should there be an endpoint url? hateoas)
        return Response.status(CREATED).entity(TransferResponse(operationToken)).build()
    }


}
