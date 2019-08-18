package com.ikhramchenkov.api

import com.google.inject.Inject
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceMovementService
import com.ikhramchenkov.service.MonthlyBalanceService
import io.dropwizard.hibernate.UnitOfWork
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.*

@Produces(APPLICATION_JSON)
class TransferOwnResource {

    @Inject
    private lateinit var accountsService: AccountsService

    @Inject
    private lateinit var balanceMovementService: BalanceMovementService

    @Inject
    private lateinit var monthlyBalanceService: MonthlyBalanceService

    @POST
    @Path("/transfer/{from}/{to}")
    @UnitOfWork
    fun transferBetweenOwnAccounts(@PathParam("from") from: String, @PathParam("to") to: String): Response {
        // 1.1 check "from" exists
        val accountFrom = accountsService.findByNumberOrThrow(from)
        // 1.2 and has enough balance

        // 2 check "to" exists
        val accountTo = accountsService.findByNumberOrThrow(to)
        // 3 lock "from" and "to" on db ordering locks by number

        // 4 "append" the movements to db
        // 5 unlock db
        // 6 send event of changing balance (would I really need to?) async way (isn't it async already?)
        // 7 return response with operation number (should there be an endpoint url? hateoas)

        Response.status(FORBIDDEN).build()
        Response.status(BAD_REQUEST).build()
        Response.status(NOT_FOUND).build()

        return Response.status(CREATED).build()
    }
}
