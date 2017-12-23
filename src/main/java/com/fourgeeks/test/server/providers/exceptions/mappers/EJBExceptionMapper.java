package com.fourgeeks.test.server.providers.exceptions.mappers;

import javax.ejb.EJBException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.util.Objects;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    @Context
    private Providers providers;

    @Override
    public Response toResponse(EJBException exception) {
        return causeToResponse(exception);
    }

    private Response causeToResponse(EJBException exception) {

        if (Objects.isNull(exception.getCausedByException())) {
            return Response.serverError().build();
        }

        Class cause = exception.getCausedByException().getClass();

        ExceptionMapper mapper = providers.getExceptionMapper(cause);
        if (Objects.isNull(mapper)) {
            return Response.serverError().build();
        } else {
            return mapper.toResponse(exception.getCausedByException());
        }
    }
}
