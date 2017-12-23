package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.domain.entities.Transaction;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.facade.TransactionFacade;
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
    public Response remove(@PathParam("id") String id) {
        final Transaction fromDB = transactionFacade.find(id);
        transactionFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }
}