package com.fourgeeks.test.server.filters;

import com.fourgeeks.test.server.annotations.PermissionAllowed;
import com.fourgeeks.test.server.domain.entities.Permission;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.providers.ErrorRegistry;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Provider
@Priority(Priorities.AUTHORIZATION)
@PermissionAllowed
public class PermissionAllowedFilter implements ContainerRequestFilter {

    @EJB
    private PersonFacade personFacade;

    @Context
    @SuppressWarnings("unused")
    private ResourceInfo resourceInfo;

    @Inject
    public PermissionAllowedFilter() {
    }

    @Override
    public void filter(ContainerRequestContext ctx) {
        List<PermissionAllowed.Permission> permissionsAllowedByMethod = Arrays.asList(resourceInfo.getResourceMethod()
                .getAnnotation(PermissionAllowed.class)
                .value());
        Person.Role role = Person.Role.valueOf(ctx.getProperty("role").toString());
        String userId = ctx.getProperty("id").toString();


        Person user = personFacade.find(userId);
        if (Objects.isNull(user) || Objects.isNull(user.getPermissions()) || user.getPermissions().isEmpty()) {
            unauthorized(ctx);
        }

        List<String> l = user.getPermissions().stream().map(Permission::getName).collect(Collectors.toList());
        if (permissionsAllowedByMethod.stream().anyMatch(p -> Arrays.asList(p.roles()).contains(role.name())
                && Arrays.stream(p.permissions()).anyMatch(item -> l.contains(item)))) {
            return;
        }
        unauthorized(ctx);
    }

    private void unauthorized(ContainerRequestContext ctx) {
        ctx.abortWith(ErrorRegistry.createResponse(Response.Status.FORBIDDEN, 609, "Access level not authorized"));
    }
}
