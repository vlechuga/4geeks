package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.domain.entities.Run;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.facade.RunFacade;
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


@Path("/runs")
public class RunResource {
    private static Logger LOG = LoggerFactory.getLogger(RunResource.class);

    @EJB
    private RunFacade runFacade;
    @EJB
    private PersonFacade personFacade;

    @Context
    private UriInfo uriInfo;

    @Context
    @SuppressWarnings("unused")
    private ContainerRequestContext ctx;

    public RunResource() {

    }

    @GET
    @RolesAllowed({"ADMIN", "RUNS_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "RUNS_TRACKER"}, permissions = "READ_ALL")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
        final List<Run> runs = runFacade.findAll();
        return Response.ok().entity(runs).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "RUNS_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "RUNS_TRACKER"}, permissions = "CREATE")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(@Valid Run run) {
        final Person user = personFacade.find(ctx.getProperty("id").toString());
        run.setCreatedBy(user);
        final Run fromDB = runFacade.create(run);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("runs")
                        .path(fromDB.getId())
                        .build())
                .entity(new ObjectId(fromDB.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "RUNS_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "RUNS_TRACKER"}, permissions = "READ")
    })
    @ValidateOwner(target = RunFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") String id) {
        final Run fromDB = runFacade.find(id);
        return Response.ok().entity(fromDB).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "RUNS_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "RUNS_TRACKER"}, permissions = "UPDATE")
    })
    @ValidateOwner(target = RunFacade.class)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") String id,
                           @Valid Run run) {
        runFacade.find(id);
        run.setId(id);
        runFacade.edit(run);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "RUNS_TRACKER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "RUNS_TRACKER"}, permissions = "DELETE")
    })
    @ValidateOwner(target = RunFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    public Response remove(@PathParam("id") String id) {
        final Run fromDB = runFacade.find(id);
        runFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }
}