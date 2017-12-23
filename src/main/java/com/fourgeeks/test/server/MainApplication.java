package com.fourgeeks.test.server;

import com.fourgeeks.test.server.providers.AuthenticationFeature;
import com.fourgeeks.test.server.services.PasswordEncryptionServiceImpl;

import com.fourgeeks.test.server.services.TokenServiceImpl;
import com.fourgeeks.test.server.services.interfaces.TokenService;
import com.nimbusds.jwt.SignedJWT;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/v1")
public class MainApplication extends ResourceConfig {
    private static Logger LOG = LoggerFactory.getLogger(MainApplication.class);

    public MainApplication() {

        setApplicationName("Four-Geeks");
        packages(true, "com.fourgeeks.test.server");
        packages(true, "com.fourgeeks.test.server.filters");
        packages(true, "com.fourgeeks.test.server.providers");

        register(EncodingFilter.class);
        register(GZipEncoder.class);
        register(DeflateEncoder.class);

        //Features
        register(AuthenticationFeature.class);
        register(RolesAllowedDynamicFeature.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(TokenServiceImpl.class).to(new TypeLiteral<TokenService<SignedJWT>>() {
                }).named("Jwt").in(Singleton.class);
            }
        });
    }
}
