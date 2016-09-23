package com.paxport.gcp.config.auth;

import com.paxport.gcp.config.global.GlobalProperty;

import org.springframework.stereotype.Component;

/**
 * Grab Secret from Datastore
 */
@Component
public class AuthSecret {

    private String secret;

    public AuthSecret(){}

    public AuthSecret(String secret){
        this.secret = secret;
    }

    public String getSecret() {
        if ( secret == null ) {
            secret = GlobalProperty.fetchValue("authentication.secret")
            .orElseThrow(() -> new RuntimeException("No authentication.secret found in global props"));
        }
        return secret;
    }
}
