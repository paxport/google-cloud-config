package com.paxport.gcp.config.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Authenticate and generate tokens
 */
@Component
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.token.header:paxport-security-token}")
    private String authTokenHeaderName = "paxport-security-token";

    @Autowired
    private AuthSecret secret;

    private SignatureAlgorithm algo = SignatureAlgorithm.HS512;

    private Key key;

    public String createNewToken(PaxportClaims newClaims, PaxportClaims principal) {
        newClaims.validateCreation(principal);
        Map<String,Object> map = newClaims.claimsMap();
        String token = Jwts.builder()
                .setClaims(map)
                .signWith(algo,ensureKey())
                .compact();
        return token;
    }

    public PaxportClaims parseHeaders(Map<String,String> requestHeaders) {
        if ( requestHeaders.containsKey(authTokenHeaderName) ) {
            return parsePaxportClaims(requestHeaders.get(authTokenHeaderName));
        }
        else {
            throw new RuntimeException("No valid security token found in request");
        }
    }

    public PaxportClaims parsePaxportClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(ensureKey())
                    .parseClaimsJws(token)
                    .getBody();
            return PaxportClaims.of(claims);
        }
        catch (RuntimeException e) {
            logger.info("parseClaims token " + token, e);
            throw new RuntimeException("Invalid Security Token");
        }
    }

    private Key ensureKey() {
        if ( key == null ) {
            try {
                byte[] encodedKey = secret.getSecret().getBytes("UTF-8");
                key = new SecretKeySpec(encodedKey, 0, encodedKey.length, algo.getValue());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return key;
    }

    public String getAuthTokenHeaderName() {
        return authTokenHeaderName;
    }

    public AuthService setSecret(AuthSecret secret) {
        this.secret = secret;
        return this;
    }
}
