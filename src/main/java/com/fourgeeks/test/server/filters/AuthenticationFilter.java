package com.fourgeeks.test.server.filters;

import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.providers.ErrorRegistry;
import com.fourgeeks.test.server.providers.TSSecurityContext;
import com.fourgeeks.test.server.services.interfaces.TokenService;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Objects;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final Provider<TokenService<SignedJWT>> tokenServiceProvider;

    @Context
    @SuppressWarnings("unused")
    private ResourceInfo resourceInfo;

    @Inject
    public AuthenticationFilter(@Named("Jwt") Provider<TokenService<SignedJWT>> tokenServiceProvider) {
        this.tokenServiceProvider = tokenServiceProvider;
    }

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        final String authorization = ctx.getHeaderString("Authorization");

        try {
            if (Objects.isNull(authorization)) {
                ctx.abortWith(ErrorRegistry
                        .createResponse(Response.Status.UNAUTHORIZED,
                                607, "Token not provided"));
            } else if (!authorization.startsWith("Bearer ")) {
                ctx.abortWith(ErrorRegistry
                        .createResponse(Response.Status.BAD_REQUEST,
                                607, "Token type is invalid"));
            } else {
                final String[] tokenParts;
                try {
                    tokenParts = authorization.split(" ");
                } catch (Exception e) {
                    ctx.abortWith(ErrorRegistry
                            .createResponse(Response.Status.UNAUTHORIZED, 607, "Token is invalid"));
                    return;
                }
                final String tokenString = tokenParts[1];
                final TokenService<SignedJWT> tokenService = tokenServiceProvider.get();
                SignedJWT signedToken = tokenService.getTokenObject(tokenString);

                if (Objects.isNull(signedToken) || !tokenService.verifyToken(signedToken)) {
                    ctx.abortWith(ErrorRegistry
                            .createResponse(Response.Status.UNAUTHORIZED, 607, "Token is invalid"));
                    return;
                }

                if (tokenService.isTokenExpired(signedToken)) {
                    ctx.abortWith(ErrorRegistry
                            .createResponse(Response.Status.UNAUTHORIZED, 607, "Token is expired"));
                    return;
                }

                final Person.Role role = Person.Role.valueOf((String) signedToken.getJWTClaimsSet().getCustomClaim("role"));
                final String email = signedToken.getJWTClaimsSet().getStringClaim("email");
                final PlainJWT jwt = new PlainJWT(signedToken.getJWTClaimsSet());
                final String subject = signedToken.getJWTClaimsSet().getSubject();

                Objects.requireNonNull(role);

                ctx.setProperty("token", jwt);
                ctx.setProperty("id", subject);
                ctx.setProperty("role", role.name());
                ctx.setProperty("email", email);

                MDC.put("token", jwt.serialize());
                MDC.put("user_id", subject);
                MDC.put("role", role.name());
                MDC.put("email", email);

                ctx.setSecurityContext(new TSSecurityContext(email, role));
            }

        } catch (Exception e) {
            ctx.abortWith(ErrorRegistry.createResponse(Response.Status.UNAUTHORIZED,
                    607, "Token is invalid or expired"));
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MDC.remove("token");
        MDC.remove("user_id");
        MDC.remove("role");
        MDC.remove("email");
    }
}