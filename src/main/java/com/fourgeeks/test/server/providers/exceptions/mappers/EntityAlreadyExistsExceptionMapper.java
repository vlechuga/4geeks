package com.fourgeeks.test.server.providers.exceptions.mappers;

import com.fourgeeks.test.server.providers.exceptions.EntityAlreadyExistsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class EntityAlreadyExistsExceptionMapper implements ExceptionMapper<EntityAlreadyExistsException> {

    @Override
    public Response toResponse(EntityAlreadyExistsException e) {
        return Response
                .status(Response.Status.NOT_ACCEPTABLE)
                .entity(e.getMessage())
                .build();
    }
}
