package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.domain.ErrorResponse;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Note;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.NoteFacade;
import com.fourgeeks.test.server.facade.PersonFacade;
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


@Path("/notes")
@Api(value = "notes",
        description = "Operations about note ex. registration, edit, etc.")
public class NoteResource {
    private static Logger LOG = LoggerFactory.getLogger(NoteResource.class);

    @EJB
    private NoteFacade noteFacade;
    @EJB
    private PersonFacade personFacade;

    @Context
    private UriInfo uriInfo;

    @Context
    @SuppressWarnings("unused")
    private ContainerRequestContext ctx;

    public NoteResource() {

    }

    @GET
    @RolesAllowed({"ADMIN", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "NOTES_MANAGER"}, permissions = "READ_ALL")
    })
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get note list", response = Note[].class)
    public Response getAll() {
        final List<Note> notes = noteFacade.findAll();
        return Response.ok().entity(notes).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "NOTES_MANAGER"}, permissions = "CREATE")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Register note")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "created", response = ObjectId.class),
            @ApiResponse(code = 400, message = "constraint violation on one or many fields"),
            @ApiResponse(code = 406, message = "This Entity already exist", response = ErrorResponse.class)
    })
    public Response register(@Valid Note note) {
        final Person user = personFacade.find(ctx.getProperty("id").toString());
        note.setCreatedBy(user);
        final Note fromDB = noteFacade.create(note);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("notes")
                        .path(fromDB.getId())
                        .build())
                .entity(new ObjectId(fromDB.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "NOTES_MANAGER"}, permissions = "READ")
    })
    @ValidateOwner(target = NoteFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get a note by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Note.class),
            @ApiResponse(code = 403, message = "Wrong owner", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response get(@PathParam("id") String id) {
        final Note fromDB = noteFacade.find(id);
        return Response.ok().entity(fromDB).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "NOTES_MANAGER"}, permissions = "UPDATE")
    })
    @ValidateOwner(target = NoteFacade.class)
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Update note")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Entity updated"),
            @ApiResponse(code = 400, message = "constraint violation on one or many fields"),
            @ApiResponse(code = 403, message = "Wrong owner", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response update(@PathParam("id") String id,
                           @Valid Note note) {
        noteFacade.find(id);
        note.setId(id);
        noteFacade.edit(note);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "NOTES_MANAGER"}, permissions = "DELETE")
    })
    @ValidateOwner(target = NoteFacade.class)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Remove note", notes = "Remove a note by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entity removed", response = Note.class),
            @ApiResponse(code = 403, message = "Wrong owner", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Entity not found", response = ErrorResponse.class)
    })
    public Response remove(@PathParam("id") String id) {
        final Note fromDB = noteFacade.find(id);
        noteFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }
}