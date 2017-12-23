package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.PersonFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Path("/users")
public class UserResource {
    private static Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @EJB
    private PersonFacade personFacade;

    @Context
    private UriInfo uriInfo;

    public UserResource() {

    }

    @GET
    @RolesAllowed("ADMIN")
    @PermissionAllowed(value = {
            @PermissionAllowed.Permission(roles = "ADMIN", permissions = "READ_ALL")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
        final List<Person> fromDB = personFacade.findAll();
        return Response.ok().entity(fromDB).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @PermissionAllowed(value = {
            @PermissionAllowed.Permission(roles = "ADMIN", permissions = "CREATE")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(@Valid Person person) {
        final Person fromDB = personFacade.create(person);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("users")
                        .path(fromDB.getEmail())
                        .build())
                .entity(new ObjectId(fromDB.getId()))
                .build();
    }

    @GET
    @Path("/{email}")
    @RolesAllowed("ADMIN")
    @PermissionAllowed(value = {
            @PermissionAllowed.Permission(roles = "ADMIN", permissions = "READ")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("email") String email) {
        final Person fromDB = personFacade.findByEmail(email);
        return Response.ok().entity(fromDB).build();
    }

    @PUT
    @Path("/{email}")
    @RolesAllowed("ADMIN")
    @PermissionAllowed(value = {
            @PermissionAllowed.Permission(roles = "ADMIN", permissions = "UPDATE")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("email") String email,
                           @Valid Person person) {
        final Person fromDB =  personFacade.findByEmail(email);
        person.setId(fromDB.getId());
        personFacade.edit(person);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{email}")
    @RolesAllowed("ADMIN")
    @PermissionAllowed(value = {
            @PermissionAllowed.Permission(roles = {"ADMIN"}, permissions = "DELETE")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response remove(@PathParam("email") String email) {
        final Person fromDB = personFacade.findByEmail(email);
        personFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }

}