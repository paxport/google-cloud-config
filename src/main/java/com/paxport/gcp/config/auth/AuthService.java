package com.paxport.gcp.config.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Authenticate and generate tokens
 */
@Component
public class AuthService implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.header:'paxport-security-token'}")
    private String headerName;

    @Autowired
    private AuthSecret secret;

    private SignatureAlgorithm algo = SignatureAlgorithm.HS512;

    private Key key;

    private Claims parseClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getSecret())
                    .parseClaimsJws(token)
                    .getBody();

            return claims;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        ensureKey();
    }
}
