package com.fourgeeks.test.server.resources.v1;

import com.fourgeeks.test.server.annotations.NoAuthentication;
import com.fourgeeks.test.server.domain.ErrorResponse;
import com.fourgeeks.test.server.domain.TokenInfo;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.facade.PersonFacade;
import com.fourgeeks.test.server.providers.ErrorRegistry;
import com.fourgeeks.test.server.providers.exceptions.UnauthorizedException;
import com.fourgeeks.test.server.services.interfaces.PasswordEncryptionService;
import com.fourgeeks.test.server.services.interfaces.TokenService;
import com.nimbusds.jwt.SignedJWT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.Objects;


@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "auth", description = "Authentication")
public class AuthResource {

    private static Logger LOG = LoggerFactory.getLogger(AuthResource.class);

    private final TokenService<SignedJWT> tokenService;
    private final PasswordEncryptionService passwordEncryptionService;

    @EJB
    private PersonFacade personFacade;

    @Inject
    public AuthResource(TokenService<SignedJWT> tokenService,
                        PasswordEncryptionService passwordEncryptionService) {
        this.tokenService = tokenService;
        this.passwordEncryptionService = passwordEncryptionService;
    }

    @GET
    @Path("/token")
    @NoAuthentication
    @ApiOperation(value = "Token generation trough basic credentials",
            notes = "Basic encoded credentials are required",
            response = TokenInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "constraint violation on one or many fields", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "user not found", response = ErrorResponse.class)
    })
    public Response generate(@HeaderParam("Authorization") String auth) throws Exception {

        final String[] usPw;
        String password, email;
        try {
            final String basicAuthString = new String(Base64.getDecoder().decode(auth.split(" ")[1]), "utf-8");
            usPw = basicAuthString.split(":");
            email = usPw[0].toLowerCase();
            password = usPw[1];
        } catch (Exception e) {
               return ErrorRegistry.createResponse(Response.Status.BAD_REQUEST,
                       604, "Unable to decode basic credentials");
        }

        Person user = personFacade.findByEmail(email);
        validateUser(user, password);
        SignedJWT token = tokenService.generateToken(user);
        TokenInfo tokenInfo = tokenService.getTokenInfo(token, false);
        return Response
                .ok(tokenInfo)
                .build();
    }

    private void validateUser(Person user, String password) {
        if (Objects.nonNull(password)) {
            boolean validPsw = user.getPassword() != null && passwordEncryptionService.check(password, user.getPassword());
            if (!validPsw) {
                throw new UnauthorizedException(604, "Error checking password");
            }
        }
        if (user.getStatus().equals(Person.Status.SUSPENDED)) {
            throw new UnauthorizedException(612, "Cannot login, user is SUSPENDED");
        }
    }
}