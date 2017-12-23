package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.domain.ObjectId;
import com.fourgeeks.test.server.domain.entities.Category;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.CategoryFacade;
import com.fourgeeks.test.server.facade.PersonFacade;
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


@Path("/categories")
public class CategoryResource {
    private static Logger LOG = LoggerFactory.getLogger(CategoryResource.class);

    @EJB
    private CategoryFacade categoryFacade;
    @EJB
    private PersonFacade personFacade;

    @Context
    private UriInfo uriInfo;

    @Context
    @SuppressWarnings("unused")
    private ContainerRequestContext ctx;

    public CategoryResource() {

    }

    @GET
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"}, permissions = "READ_ALL")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
        final List<Category> categories = categoryFacade.findAll();
        return Response.ok().entity(categories).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"}, permissions = "CREATE")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(@Valid Category category) {

        final Person user = personFacade.find(ctx.getProperty("id").toString());
        category.setCreatedBy(user);
        final Category newCategary = categoryFacade.create(category);
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("categories")
                        .path(newCategary.getId())
                        .build())
                .entity(new ObjectId(newCategary.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"}, permissions = "READ")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@PathParam("id") String id) {
        final Category category = categoryFacade.find(id);
        return Response.ok().entity(category).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"}, permissions = "UPDATE")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") String id,
                           @Valid Category category) {
        categoryFacade.find(id);
        category.setId(id);
        categoryFacade.edit(category);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"})
    @PermissionAllowed({
            @PermissionAllowed.Permission(roles = {"ADMIN", "EXPENSE_TRACKER", "NOTES_MANAGER"}, permissions = "DELETE")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response remove(@PathParam("id") String id) {
        final Category fromDB = categoryFacade.find(id);
        categoryFacade.remove(fromDB);
        return Response.ok().entity(fromDB).build();
    }
}