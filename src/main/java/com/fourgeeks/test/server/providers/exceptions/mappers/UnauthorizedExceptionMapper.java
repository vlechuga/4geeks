package com.fourgeeks.test.server.providers.exceptions.mappers;

import com.fourgeeks.test.server.providers.ErrorRegistry;
import com.fourgeeks.test.server.providers.exceptions.UnauthorizedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException e) {
        return ErrorRegistry.createResponse(Response.Status.UNAUTHORIZED, e.getCode(), e.getMessage());
    }

}
