package com.paxport.gcp.config.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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

    // 1 day from now
    private Date expires() {
        return Date.from(ZonedDateTime.now().plusDays(1).toInstant());
    }

    public String createNewToken(PaxportClaims newClaims, PaxportClaims principal) {
        newClaims.validateCreation(principal);
        Map<String,Object> map = newClaims.claimsMap();
        JwtBuilder builder = Jwts.builder().setClaims(map);
        if ( newClaims.isInternal() ) {
            // expire internal tokens after one day
            builder.setExpiration(expires());
        }
        return builder.signWith(algo,ensureKey()).compact();
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
            Date expires = claims.getExpiration();
            if ( expires != null && new Date().after(expires) ) {
                throw new UnauthorizedException("Internal Security Token expired at: " + expires);
            }
            return PaxportClaims.of(claims);
        }
        catch (UnauthorizedException e) {
            throw e;
        }
        catch (RuntimeException e) {
            logger.warn("parseClaims token " + token, e);
            throw new UnauthorizedException("Invalid Security Token");
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
