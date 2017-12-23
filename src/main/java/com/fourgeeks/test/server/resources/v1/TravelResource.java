package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.domain.entities.Travel;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.facade.TravelFacade;
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


@Path("/travels")
public class TravelResource {
    private static Logger LOG = LoggerFactory.getLogger(TravelResource.class);

    @EJB
    private TravelFacade travelFacade;
    @EJB
    private PersonFacade personFacade;

    @Context
    private UriInfo uriInfo;

    @Context
    @SuppressWarnings("unused")
    private ContainerRequestContext ctx;

    public TravelResource() {

    }

    @GET
    @RolesAllowed({"ADMIN", "TRAVEL_PLANNER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "TRAVEL_PLANNER"}, permissions = "READ_ALL")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
        final List<Travel> travels = travelFacade.findAll();
        return Response.ok().entity(travels).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "TRAVEL_PLANNER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "TRAVEL_PLANNER"}, permissions = "CREATE")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(@Valid Travel travel) {
        final Person user = personFacade.find(ctx.getProperty("id").toString());
        travel.setCreatedBy(user);
        final Travel fromDB = travelFacade.create(travel);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("travels")
                        .path(fromDB.getId())
                        .build())
                .entity(new ObjectId(fromDB.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "TRAVEL_PLANNER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "TRAVEL_PLANNER"}, permissions = "READ")
    })
    @ValidateOwner(target = TravelFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") String id) {
        final Travel fromDB = travelFacade.find(id);
        return Response.ok().entity(fromDB).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "TRAVEL_PLANNER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "TRAVEL_PLANNER"}, permissions = "UPDATE")
    })
    @ValidateOwner(target = TravelFacade.class)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") String id,
                           @Valid Travel travel) {
        travelFacade.find(id);
        travel.setId(id);
        travelFacade.edit(travel);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "TRAVEL_PLANNER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "TRAVEL_PLANNER"}, permissions = "DELETE")
    })
    @ValidateOwner(target = TravelFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    public Response remove(@PathParam("id") String id) {
        final Travel fromDB = travelFacade.find(id);
        travelFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }
}