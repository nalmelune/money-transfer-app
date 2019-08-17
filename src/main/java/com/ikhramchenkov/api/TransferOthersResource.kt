package com.ikhramchenkov.api

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.*

@Produces(APPLICATION_JSON)
class TransferOthersResource {

    @POST
    @Path("/reserve")
    fun reserveAttemptToSend(): Response {

        Response.status(FORBIDDEN).build()
        Response.status(BAD_REQUEST).build()
        Response.status(NOT_FOUND).build()

        return Response.ok()
                //                .entity()
                .build()
    }

    @POST
    @Path("/confirm/{token}")
    fun confirmSending(@PathParam("token") token: String): Response {
        Response.status(FORBIDDEN).build()
        Response.status(NOT_FOUND).build()

        return Response.ok()
                //                .entity()
                .build()
    }

    @POST
    @Path("/cancel/{token}")
    fun cancelSending(@PathParam("token") token: String): Response {

        Response.status(FORBIDDEN).build()
        Response.status(NOT_FOUND).build()

        return Response.ok()
                //                .entity()
                .build()
    }
}
