package com.fourgeeks.test.server.providers;

import com.fourgeeks.test.server.annotations.NoAuthentication;
import com.fourgeeks.test.server.annotations.ValidateOwner;
import com.fourgeeks.test.server.filters.AuthenticationFilter;
import com.fourgeeks.test.server.filters.ValidateOwnerFilter;
import com.fourgeeks.test.server.services.interfaces.TokenService;
import com.nimbusds.jwt.SignedJWT;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

public class AuthenticationFeature implements DynamicFeature {

    private final TokenService<SignedJWT> tokenService;

    @Inject
    public AuthenticationFeature(TokenService<SignedJWT> tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {

        if (resourceInfo.getResourceClass().getName().contains("Resource")
                && !resourceInfo.getResourceMethod().isAnnotationPresent(NoAuthentication.class)) {

            if (resourceInfo.getResourceClass().getCanonicalName()
                    .equals("org.glassfish.jersey.server.wadl.internal.WadlResource"))
                return;

            featureContext.register(AuthenticationFilter.class);

            if (resourceInfo.getResourceMethod().isAnnotationPresent(ValidateOwner.class))
                featureContext.register(ValidateOwnerFilter.class);
        }
    }
}