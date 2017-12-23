package com.fourgeeks.test.server.filters;

import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.domain.entities.CreatedBy;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.*;
import com.fourgeeks.test.server.providers.ErrorRegistry;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Priority(Priorities.USER)
@ValidateOwner
public class ValidateOwnerFilter implements ContainerRequestFilter {

    @EJB
    private CategoryFacade categoryFacade;
    @EJB
    private NoteFacade noteFacade;
    @EJB
    private RunFacade runFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private TravelFacade travelFacade;

    @Context
    @SuppressWarnings("unused")
    private ResourceInfo resourceInfo;

    public ValidateOwnerFilter() {
    }

    @Override
    public void filter(ContainerRequestContext ctx) {

        Class<?> clazz = resourceInfo.getResourceMethod().getAnnotation(ValidateOwner.class).target();

        final String id = (String) ctx.getProperty("id");
        final Person.Role role = Person.Role.valueOf((String) ctx.getProperty("role"));
        final String entityId = ctx.getUriInfo().getPathParameters().get("id").get(0);

        if (role == Person.Role.ADMIN) {
            return;
        }

        CreatedBy fromDB = null;
        switch (clazz.getName()) {
            case "CategoryFacade":
                fromDB = categoryFacade.find(entityId);
                break;
            case "NoteFacade":
                fromDB = noteFacade.find(entityId);
                break;
            case "RunFacade":
                fromDB = runFacade.find(entityId);
                break;
            case "TransactionFacade":
                fromDB = transactionFacade.find(entityId);
                break;
            case "TravelFacade":
                fromDB = travelFacade.find(entityId);
                break;
        }

        if (Objects.isNull(fromDB) || !fromDB.getCreatedBy().getId().equals(id)) {
            ctx.abortWith(Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(ErrorRegistry
                            .createError(613, "you don't own the registry"))
                    .build());

        }

    }
}