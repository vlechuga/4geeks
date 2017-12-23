package com.fourgeeks.test.server.providers;

import com.fourgeeks.test.server.domain.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class ErrorRegistry {

    private static Map<Integer, String> reg = new HashMap<Integer, String>() {{
        put(600, "unhandled error");
        put(601, "constraint violation error(s)");
        put(602, "entity already exist");
        put(603, "entity doesn't exist");
        put(604, "invalid parameter");
        put(605, "user is SUSPENDED");
        put(606, "invalid password");
        put(607, "invalid token");
        put(608, "token generation error");
        put(609, "not authorized");
        put(610, "invalid password");
        put(611, "already registered");
        put(612, "access level not authorized");
        put(613, "Forbidden to update this registry");

    }};

    public static String getMessage(Integer number) {
        return reg.get(number);
    }

    public static ErrorResponse createError(Integer number, String description) {
        return new ErrorResponse(number, reg.get(number), description);
    }

    public static ErrorResponse createError(Integer number, String description, String userDescription) {
        return new ErrorResponse(number, reg.get(number), description, userDescription);
    }

    public static Response createResponse(Response.Status status, Integer number, String description) {
        return Response.status(status)
                .entity(createError(number, description))
                .build();
    }

    public static Response createResponse(Response.Status status, Integer number, String description, String userDescription) {
        return Response.status(status)
                .entity(createError(number, description, userDescription))
                .build();
    }

}
