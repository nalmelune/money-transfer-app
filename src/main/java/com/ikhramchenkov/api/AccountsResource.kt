package com.ikhramchenkov.api

import com.google.inject.Inject
import com.ikhramchenkov.dto.AccountInfoDto
import com.ikhramchenkov.entity.AccountEntity
import com.ikhramchenkov.exception.AttemptToUseStrangersAccountException
import com.ikhramchenkov.service.AccountsService
import com.ikhramchenkov.service.BalanceService
import io.dropwizard.hibernate.UnitOfWork
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.OK

@Path("/accounts")
@Produces(APPLICATION_JSON)
class AccountsResource @Inject constructor(
    private val accountsService: AccountsService,
    private val balanceService: BalanceService
) {

    /**
     * Let's assume user never has too many accounts to load, so pagination is not required
     */
    @GET
    @UnitOfWork(transactional = false)
    fun allAvailableAccounts(@QueryParam("ownerId") ownerId: Long?): Response {
        // let's pretend it's our implementation of authentication
        if (ownerId == null) throw BadRequestException("ownerId is not set")

        val accountInfoDTOs = accountsService.findByOwner(ownerId).map { account ->
            toAccountInfoDto(account, balanceService.getAccountBalance(account.accountNumber!!))
        }

        return Response.status(OK).entity(accountInfoDTOs).build();
    }

    // Get more account details
    @GET
    @Path("/{number}")
    fun accountDetails(
        @PathParam("number") accountNumber: String,
        @QueryParam("ownerId") ownerId: Long?
    ): Response {
        // let's pretend it's our implementation of authentication
        if (ownerId == null) throw BadRequestException("ownerId is not set")

        val accountInfoDto = accountsService.findByNumberOrThrow(accountNumber).let { account ->
            if (ownerId != account.ownerId) throw AttemptToUseStrangersAccountException()

            toAccountInfoDto(account, balanceService.getAccountBalance(account.accountNumber!!))
        }

        return Response.status(OK).entity(accountInfoDto).build();
    }

    private fun toAccountInfoDto(
        account: AccountEntity,
        balance: Long
    ) = AccountInfoDto(account.accountNumber!!, account.accountType!!, account.description!!, balance)
}
