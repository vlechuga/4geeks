package com.fourgeeks.test.server.services;

import com.fourgeeks.test.server.domain.TokenInfo;
import com.fourgeeks.test.server.domain.entities.Permission;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.providers.exceptions.TokenException;
import com.fourgeeks.test.server.services.interfaces.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Service
public class TokenServiceImpl implements TokenService<SignedJWT> {

    private static Logger LOG = LoggerFactory.getLogger(TokenServiceImpl.class);

    private static final int TOKEN_EXP = 7;

    public final ResourceBundle config = ResourceBundle.getBundle("config", new Locale("es"));


    private final JWSSigner signer;
    private final byte[] sharedKey;


    public TokenServiceImpl() {
        this.sharedKey = Base64.getDecoder().decode(config.getString("token.shared.key"));
        this.signer = new MACSigner(sharedKey);
    }

    @Override
    public SignedJWT generateToken(Person user) throws TokenException {
        JWTClaimsSet claimsSet = new JWTClaimsSet();

        claimsSet.setSubject(user.getId());
        claimsSet.setIssuer("jwt-service");
        final Date now = new Date();
        claimsSet.setIssueTime(now);
        claimsSet.setNotBeforeTime(now);
        claimsSet.setJWTID(UUID.randomUUID().toString());
        claimsSet.setExpirationTime(selectExpirationTime());

        claimsSet.setAudience("developer-test");
        claimsSet.setCustomClaim("role", user.getRole().name());
        claimsSet.setCustomClaim("email", user.getEmail());

        if (Objects.nonNull(user.getPermissions()) && !user.getPermissions().isEmpty()) {
            claimsSet.setCustomClaim("permissions", user.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));
        }

        try {
            return signToken(new PlainJWT(claimsSet));
        } catch (Exception e) {
            LOG.error("Error generating token for user " + user.getId(), e);
            throw new TokenException("Error generating token for user " + user.getId(), e);
        }
    }

    @Override
    public TokenInfo getTokenInfo(SignedJWT token, boolean encrypted) throws TokenException {
        try {
            final ReadOnlyJWTClaimsSet claimsSet = token.getJWTClaimsSet();
            String stringToken = encrypted ? encryptToken(token) : token.serialize();

            return new TokenInfo(
                    claimsSet.getSubject(),
                    "Bearer",
                    stringToken);
        } catch (Exception e) {
            LOG.error("Error generating tokenInfo", e);
            throw new TokenException("Error generating tokenInfo", e);
        }
    }

    @Override
    public String encryptToken(SignedJWT signedJWT) throws Exception {

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).contentType("JWT").build(),
                new Payload(signedJWT));
        jweObject.encrypt(new DirectEncrypter(sharedKey));

        return jweObject.serialize();
    }

    @Override
    public SignedJWT decryptToken(String jweString) throws Exception {
        JWEObject jweObject = JWEObject.parse(jweString);
        jweObject.decrypt(new DirectDecrypter(sharedKey));

        return jweObject.getPayload().toSignedJWT();
    }

    @Override
    public boolean isTokenExpired(SignedJWT token) throws Exception {
        return new Date().after(token.getJWTClaimsSet().getExpirationTime());
    }

    @Override
    public boolean verifyToken(SignedJWT token) throws Exception {
        JWSVerifier verifier = new MACVerifier(sharedKey);
        return token.verify(verifier);
    }

    @Override
    public SignedJWT getTokenObject(String tokenString) throws Exception {
        try {
            return SignedJWT.parse(tokenString);
        } catch (ParseException e) {
            return decryptToken(tokenString);
        }
    }

    private SignedJWT signToken(PlainJWT token) throws JOSEException, ParseException {
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), token.getJWTClaimsSet());
        signedJWT.sign(signer);
        return signedJWT;
    }

    private Date selectExpirationTime() {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, TOKEN_EXP);
        return instance.getTime();
    }

}
