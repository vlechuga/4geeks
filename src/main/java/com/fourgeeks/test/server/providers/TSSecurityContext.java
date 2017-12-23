package com.fourgeeks.test.server.providers;

import com.fourgeeks.test.server.domain.entities.Person;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class TSSecurityContext implements SecurityContext {

    private final String userEmail;
    private final Person.Role userRole;

    public TSSecurityContext(String userEmail, Person.Role userRole) {
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> userEmail;
    }

    @Override
    public boolean isUserInRole(String s) {
        return userRole.name().equals(s);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}