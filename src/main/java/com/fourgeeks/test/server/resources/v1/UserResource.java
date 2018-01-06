package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.domain.ErrorResponse;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Category;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.mappers.UserMapper;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Path("/users")
@Api(value = "users",
        description = "Operations about user ex. registration, edit, etc.")
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
    @ApiOperation(value = "Get user list",
            notes = "Only for administration purposes",
            response = Person[].class)
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
    @ApiOperation(value = "Register category",
            notes = "Only for administration purposes")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "created", response = ObjectId.class),
            @ApiResponse(code = 400, message = "constraint violation on one or many fields"),
            @ApiResponse(code = 406, message = "This Entity already exist", response = ErrorResponse.class)
    })
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
    @ApiOperation(value = "Get user by email",
            notes = "Only for administration purposes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Person.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response get(@PathParam("email") String email) {
        final Person fromDB = personFacade.findByEmail(email);
        return Response.ok().entity(UserMapper.INSTANCE.personToPersonDto(fromDB)).build();
    }

    @PUT
    @Path("/{email}")
    @RolesAllowed("ADMIN")
    @PermissionAllowed(value = {
            @PermissionAllowed.Permission(roles = "ADMIN", permissions = "UPDATE")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Update user",
            notes = "Only for administration purposes")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Entity updated"),
            @ApiResponse(code = 400, message = "constraint violation on one or many fields"),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
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
    @ApiOperation(value = "Remove user", notes = "Remove a user by email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity removed", response = Category.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response remove(@PathParam("email") String email) {
        final Person fromDB = personFacade.findByEmail(email);
        personFacade.remove(fromDB);
        return Response.ok().entity(UserMapper.INSTANCE.personToPersonDto(fromDB)).build();
    }

}