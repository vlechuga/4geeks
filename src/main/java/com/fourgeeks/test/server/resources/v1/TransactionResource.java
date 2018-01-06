package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.domain.ErrorResponse;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.domain.entities.Transaction;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.facade.TransactionFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Path("/transactions")
@Api(value = "transactions",
        description = "Operations about transaction ex. registration, edit, etc.")
public class TransactionResource {
    private static Logger LOG = LoggerFactory.getLogger(TransactionResource.class);

    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private PersonFacade personFacade;

    @Context
    private UriInfo uriInfo;

    @Context
    @SuppressWarnings("unused")
    private ContainerRequestContext ctx;

    public TransactionResource() {

    }

    @GET
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER"}, permissions = "READ_ALL")
    })
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get run list", response = Transaction[].class)
    public Response getAll() {
        final List<Transaction> runs = transactionFacade.findAll();
        return Response.ok().entity(runs).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER"}, permissions = "CREATE")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Register transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "created", response = ObjectId.class),
            @ApiResponse(code = 400, message = "constraint violation on one or many fields"),
            @ApiResponse(code = 406, message = "This Entity already exist", response = ErrorResponse.class)
    })
    public Response register(@Valid Transaction transaction) {
        final Person user = personFacade.find(ctx.getProperty("id").toString());
        transaction.setCreatedBy(user);
        final Transaction fromDB = transactionFacade.create(transaction);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("transactions")
                        .path(fromDB.getId())
                        .build())
                .entity(new ObjectId(fromDB.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER"}, permissions = "READ")
    })
    @ValidateOwner(target = TransactionFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get transaction",
            notes = "Get a run by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Transaction.class),
            @ApiResponse(code = 403, message = "Wrong owner", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response get(@PathParam("id") String id) {
        final Transaction fromDB = transactionFacade.find(id);
        return Response.ok().entity(fromDB).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER"}, permissions = "UPDATE")
    })
    @ValidateOwner(target = TransactionFacade.class)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Update transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Entity updated"),
            @ApiResponse(code = 400, message = "constraint violation on one or many fields"),
            @ApiResponse(code = 403, message = "Wrong owner", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response update(@PathParam("id") String id,
                           @Valid Transaction transaction) {
        transactionFacade.find(id);
        transaction.setId(id);
        transactionFacade.edit(transaction);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER"}, permissions = "DELETE")
    })
    @ValidateOwner(target = TransactionFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Remove transaction",
            notes = "Remove a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity removed", response = Transaction.class),
            @ApiResponse(code = 403, message = "Wrong owner", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response remove(@PathParam("id") String id) {
        final Transaction fromDB = transactionFacade.find(id);
        transactionFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }
}