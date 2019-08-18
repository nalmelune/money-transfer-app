package com.ikhramchenkov.api

import com.google.inject.Inject
import com.ikhramchenkov.dto.BalanceDto
import com.ikhramchenkov.service.AccountsService
import io.dropwizard.hibernate.UnitOfWork
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.*

@Path("/accounts")
@Produces(APPLICATION_JSON)
class AccountsResource {

    @Inject
    private lateinit var accountsService: AccountsService

    /**
     * Let's assume user never has too many accounts to load, so pagination is not required
     */
    @GET
    @UnitOfWork(transactional = false)
    fun allAvailableAccounts(): Response {
        val findById = accountsService.findById(1)

        Response.status(FORBIDDEN).build()
        return Response.status(OK).entity(BalanceDto("43213412313123413", 34562L)).build();
    }

    // Get more account details
    @GET
    @Path("/{number}")
    fun accountDetails(@PathParam("number") number: String): Response {
        Response.status(FORBIDDEN).build()
        Response.status(BAD_REQUEST).build()
        Response.status(NOT_FOUND).build()
        return Response.status(OK).entity(BalanceDto("43213412313123413", 34562L)).build();
    }

    @GET
    @Path("/{number}/balance")
    fun balance(@PathParam("number") number: String): Response {
        Response.status(FORBIDDEN).build()
        Response.status(BAD_REQUEST).build()
        Response.status(NOT_FOUND).build()

        return Response.status(OK)
            .entity(BalanceDto("43213412313123413", 34562L))
            .build()
    }
}
